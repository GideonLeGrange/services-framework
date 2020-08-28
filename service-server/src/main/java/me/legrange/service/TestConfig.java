package me.legrange.service;

import javax.validation.constraints.NotBlank;

public interface TestConfig extends ServiceConfiguration {

    @NotBlank(message = "Name must not be blank")
    String getName() ;

    boolean isAllowed() ;

    Integer getSize();

}
