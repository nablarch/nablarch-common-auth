package nablarch.common.permission;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.SortedSet;

import nablarch.core.ThreadContext;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class PermissionUtilTest {

    @Before
    public void setUp() throws Exception {
        ThreadContext.clear();
    }

    @After
    public void tearDown() throws Exception {
        ThreadContext.clear();
    }

    @Test
    public void testAccessor() {

        Permission permission = new Permission() {
            public boolean permit(String requestId) {
                return false;
            }
            public SortedSet<String> getRequestIds() {
                return null;
            }
        };
        PermissionUtil.setPermission(permission);
        assertThat(PermissionUtil.getPermission(), is(sameInstance(permission)));
    }
}
