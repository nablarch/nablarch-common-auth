package nablarch.common.availability;

import nablarch.core.repository.ObjectLoader;
import nablarch.core.repository.SystemRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServiceAvailabilityUtilTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final ServiceAvailability serviceAvailability = mock(ServiceAvailability.class);

    @Before
    public void setUp() throws Exception {
        SystemRepository.clear();
        SystemRepository.load(new ObjectLoader() {
            @Override
            public Map<String, Object> load() {
                final Map<String, Object> result = new HashMap<String, Object>();
                result.put("serviceAvailability", serviceAvailability);
                return result;
            }
        });
    }

    @Test
    public void testServiceAvailableOkStatus() {
        when(serviceAvailability.isAvailable("REQ0000001")).thenReturn(true);

        assertThat(ServiceAvailabilityUtil.isAvailable("REQ0000001"), is(true));
    }

    @Test
    public void testServiceUnAvailableNgStatus() {
        when(serviceAvailability.isAvailable("REQ0000001")).thenReturn(false);

        assertThat(ServiceAvailabilityUtil.isAvailable("REQ0000001"), is(false));
    }

    /**
     * {@link ServiceAvailabilityUtil#isAvailable)}のテスト。
     *
     * @throws Exception
     */
    @Test
    public void testServiceAvailableNullStatus() throws Exception {
        SystemRepository.clear();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("specified serviceAvailability is not registered in SystemRepository.");
        ServiceAvailabilityUtil.isAvailable("REQ0000001");
    }
}
