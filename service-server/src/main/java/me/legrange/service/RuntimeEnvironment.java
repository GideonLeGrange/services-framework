package me.legrange.service;


import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.String.format;

/**
 * A utility class that provides access to OS/runtime environment relate information and operations.
 */
public class RuntimeEnvironment {

    private static final String OS_NAME = "os.name";
    private static final String OS_ARCH = "os.arch";
    private static final String TMP_DIR = "java.io.tmpdir";
    /**
     * Remember the OS type once it's been detected
     */
    private static Type type;


    /**
     * Types of 'thought to be supported' operating systems.
     */
    public enum Type {
        WINDOWS("Windows"), LINUX("Linux"), MACOS("MacOS");

        private final String description;
        Type(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    /**
     * Detect if the application is running inside a container. This is heuristic based and will
     * need to be expanded for different container framerworks.
     *
     * @return True if we thinkk it is in a container
     */
    public static boolean isInContainer() {
        try (Stream<String> in = Files.lines(Paths.get("/proc/1/cgroup"))) {
            return in.anyMatch(line -> line.contains("/docker"));
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Get the CPU architecture.
     *
     * @return The CPU architeture
     */
    public static String getArch() {
        String arch = System.getProperty(OS_ARCH);
        switch (arch) {
            case "amd64":
                return "x86_64";
            case "arm":
                return "armv7l";
        }
        return arch;
    }

    /**
     * Return the number of CPU cores.
     *
     * @return The number of cores
     */
    public static int getCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * Return if IPv6 is supported.
     *
     * @return True if it is
     * @throws EnvironmentDetectionException
     */
    public static boolean isIpv6Supported() throws EnvironmentDetectionException {
        return haveAddressesOfType(Inet6Address.class);
    }

    /**
     * Return if IPv4 is supported.
     *
     * @return True if it is
     * @throws EnvironmentDetectionException
     */
    public static boolean isIpv4Supported() throws EnvironmentDetectionException {
        return haveAddressesOfType(Inet4Address.class);
    }

    /**
     * Return the path of the JAR file containing the running application.
     *
     * @return The path to the JAR
     */
    public static String getApplicationJarName() throws EnvironmentDetectionException {
        try {
            URI uri = RuntimeEnvironment.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            if (uri == null) {
                throw new EnvironmentDetectionException("Cannot determining application JAR path - code source location is null");
            }
            return new File(uri).getPath();
        } catch (URISyntaxException e) {
            throw new EnvironmentDetectionException(format("Error determining application JAR path (%s)", e.getMessage()), e);
        }
    }

    /**
     * Check if the runtime environment has interfaces with addresses of the given type. Used for IPv4 and IPv6 detection.
     *
     * @param type The type of address
     * @return True if it has
     * @throws EnvironmentDetectionException
     */
    private static boolean haveAddressesOfType(Class<? extends InetAddress> type) throws EnvironmentDetectionException {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                boolean haveFam = iface.getInterfaceAddresses().stream()
                        .filter(address -> type.isAssignableFrom(address.getAddress().getClass()))
                        .findFirst().isPresent();
                if (haveFam) {
                    return true;
                }
            }
        } catch (SocketException e) {
            throw new EnvironmentDetectionException(format("Error checking for IPv6 (%s)", e.getMessage()), e);
        }
        return false;
    }


    public static String getTempDir() {
        String temp = System.getProperty(TMP_DIR);
        if ((temp == null) || temp.isEmpty()) {
            throw new UnsupportedOperationException(format("Temporary directory is not specified"));
        }
        return temp;
    }

    public static String getComputerName() {
        Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME")) {
            return env.get("COMPUTERNAME");
        } else if (env.containsKey("HOSTNAME")) {
            return env.get("HOSTNAME");
        } else {
            return format("Unknown (%s/%s)", getArch(), getOsType());
        }
    }


    /**
     * Apply heuristics to determine the OS type from the os.name system property.
     *
     * @return The OS Type
     */
    public static Type getOsType() {
        if (type == null) {
            String osName = System.getProperty(OS_NAME).toLowerCase();
            if ((osName.indexOf("win") >= 0)) {
                type = Type.WINDOWS;
            } else if (osName.indexOf("mac") >= 0) {
                type = Type.MACOS;
            } else if (osName.indexOf("inux") >= 0) {
                type = Type.LINUX;
            } else {
                throw new UnsupportedOperationException(format("Unknown OS (%s)", osName));
            }
        }
        return type;
    }

    private RuntimeEnvironment() {
    }
}