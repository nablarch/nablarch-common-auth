package nablarch.common.permission;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;


/**
 * {@link BasicPermission}のテストクラス。
 *
 * @author Kiyohito Itoh
 */
public class BasicPermissionTest {
    
    /**
     * {@link BasicPermission#permit(String)}のテスト。
     */
    @Test
    public void testGetPermission() {
        
        Permission permissionWithNullRequestIds = new BasicPermission(null);
        assertFalse(permissionWithNullRequestIds.permit(null));
        assertFalse(permissionWithNullRequestIds.permit("unknown"));
        
        Permission permissionWithEmptyRequestIds = new BasicPermission(new TreeSet<String>());
        assertFalse(permissionWithEmptyRequestIds.permit(null));
        assertFalse(permissionWithEmptyRequestIds.permit("unknown"));
        
        Permission permission = new BasicPermission(new TreeSet<String>(Arrays.asList(new String[] {"req1", "req2"})));
        assertFalse(permission.permit(null));
        assertFalse(permission.permit("dummy"));
        assertTrue(permission.permit("req1"));
        assertTrue(permission.permit("req1"));
        assertFalse(permission.permit("req3"));
        
        SortedSet<String> requestIds = permission.getRequestIds();
        Iterator<String> itr = requestIds.iterator();
        assertEquals("req1", itr.next());
        assertEquals("req2", itr.next());
        assertFalse(itr.hasNext());
    }
}
