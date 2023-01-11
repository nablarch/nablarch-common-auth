package nablarch.common.authorization.role;

import nablarch.core.ThreadContext;
import nablarch.core.repository.ObjectLoader;
import nablarch.core.repository.SystemRepository;
import nablarch.fw.ExecutionContext;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

/**
 * {@link CheckRoleUtil}の単体テスト。
 *
 * @author Tanaka Tomoyuki
 */
public class CheckRoleUtilTest {
    private final MockRoleEvaluator mockRoleEvaluator = new MockRoleEvaluator();
    private final ExecutionContext context = new ExecutionContext();

    @Before
    public void setUp() {
        ThreadContext.clear();
        ThreadContext.setUserId("test-user");

        SystemRepository.clear();
        SystemRepository.load(new ObjectLoader() {
            @Override
            public Map<String, Object> load() {
                Map<String, Object> objects = new HashMap<String, Object>();
                objects.put("roleEvaluator", mockRoleEvaluator);
                return objects;
            }
        });
    }

    /**
     * checkRoleメソッドのテスト({@link RoleEvaluator}のメソッドと連携できていることを確認)。
     */
    @Test
    public void testCollaborateWithRoleEvaluatorWhenCallCheckRole() {
        CheckRoleUtil.checkRole("FOO", context);

        assertThat(mockRoleEvaluator.calledMethodName, is("evaluateAllOf"));
        assertThat(mockRoleEvaluator.userId, is(ThreadContext.getUserId()));
        assertThat(mockRoleEvaluator.roles, contains("FOO"));
        assertThat(mockRoleEvaluator.context, is(sameInstance(context)));
    }

    /**
     * checkRoleメソッドのテスト({@link RoleEvaluator}が返した結果をそのまま返却していること)。
     */
    @Test
    public void testReturnValueEqualToResultOfRoleEvaluatorWhenCallCheckRole() {
        mockRoleEvaluator.returnValue = true;
        assertThat(CheckRoleUtil.checkRole("FOO", context), is(true));

        mockRoleEvaluator.returnValue = false;
        assertThat(CheckRoleUtil.checkRole("FOO", context), is(false));
    }

    /**
     * checkRoleAllOfメソッドのテスト({@link RoleEvaluator}のメソッドと連携できていることを確認)。
     */
    @Test
    public void testCollaborateWithRoleEvaluatorWhenCallCheckRoleAllOf() {
        CheckRoleUtil.checkRoleAllOf(Arrays.asList("FOO", "BAR"), context);

        assertThat(mockRoleEvaluator.calledMethodName, is("evaluateAllOf"));
        assertThat(mockRoleEvaluator.userId, is(ThreadContext.getUserId()));
        assertThat(mockRoleEvaluator.roles, contains("FOO", "BAR"));
        assertThat(mockRoleEvaluator.context, is(sameInstance(context)));
    }

    /**
     * checkRoleAllOfメソッドのテスト({@link RoleEvaluator}が返した結果をそのまま返却していること)。
     */
    @Test
    public void testReturnValueEqualToResultOfRoleEvaluatorWhenCallCheckRoleAllOf() {
        mockRoleEvaluator.returnValue = true;
        assertThat(CheckRoleUtil.checkRoleAllOf(Arrays.asList("FOO", "BAR"), context), is(true));

        mockRoleEvaluator.returnValue = false;
        assertThat(CheckRoleUtil.checkRoleAllOf(Arrays.asList("FOO", "BAR"), context), is(false));
    }

    /**
     * checkRoleAnyOfメソッドのテスト({@link RoleEvaluator}のメソッドと連携できていることを確認)。
     */
    @Test
    public void testCollaborateWithRoleEvaluatorWhenCallCheckRoleAnyOf() {
        CheckRoleUtil.checkRoleAnyOf(Arrays.asList("FOO", "BAR"), context);

        assertThat(mockRoleEvaluator.calledMethodName, is("evaluateAnyOf"));
        assertThat(mockRoleEvaluator.userId, is(ThreadContext.getUserId()));
        assertThat(mockRoleEvaluator.roles, contains("FOO", "BAR"));
        assertThat(mockRoleEvaluator.context, is(sameInstance(context)));
    }

    /**
     * checkRoleAnyOfメソッドのテスト({@link RoleEvaluator}が返した結果をそのまま返却していること)。
     */
    @Test
    public void testReturnValueEqualToResultOfRoleEvaluatorWhenCallCheckRoleAnyOf() {
        mockRoleEvaluator.returnValue = true;
        assertThat(CheckRoleUtil.checkRoleAnyOf(Arrays.asList("FOO", "BAR"), context), is(true));

        mockRoleEvaluator.returnValue = false;
        assertThat(CheckRoleUtil.checkRoleAnyOf(Arrays.asList("FOO", "BAR"), context), is(false));
    }
}