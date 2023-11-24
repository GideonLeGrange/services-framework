package me.legrange.services.helicopterorm;

import com.heliorm.Orm;
import com.heliorm.OrmException;
import com.heliorm.OrmTransaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

final class ProxyOrm implements InvocationHandler {

    private Orm orm;
    private final OrmPool pool;

    public ProxyOrm(OrmPool pool) {
        this.pool = pool;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (orm == null) {
            orm = pool.issue();
        }
        if (method.getName().equals("close")) {
            try {
                orm.close();
                return null;
            }
            finally {
                pool.release(orm);
                orm = null;
            }
        }
        return method.invoke(orm, args);
    }

}
