package nablarch.common.availability;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import nablarch.core.repository.ObjectLoader;
import nablarch.core.repository.SystemRepository;
import nablarch.test.support.db.helper.VariousDbTestHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import junit.framework.Assert;

import mockit.Expectations;
import mockit.Mocked;

public class ServiceAvailabilityUtilTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mocked
    private ServiceAvailability serviceAvailability;

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
        new Expectations() {{
            serviceAvailability.isAvailable("REQ0000001");
            result = true;
        }};

        assertThat(ServiceAvailabilityUtil.isAvailable("REQ0000001"), is(true));
    }

    @Test
    public void testServiceUnAvailableNgStatus() {
        new Expectations() {{
            serviceAvailability.isAvailable("REQ0000001");
            result = false;
        }};

        assertThat(ServiceAvailabilityUtil.isAvailable("REQ0000001"), is(false));
    }

    /**
     * {@link ServiceAvailabilityUtil#getServiceAvailability()}のテスト。
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
