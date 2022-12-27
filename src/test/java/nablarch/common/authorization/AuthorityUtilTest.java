package nablarch.common.authorization;

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
 * {@link AuthorityUtil}の単体テスト。
 *
 * @author Tanaka Tomoyuki
 */
public class AuthorityUtilTest {
    private final MockAuthorityEvaluator mockAuthorityEvaluator = new MockAuthorityEvaluator();
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
                objects.put("authorityEvaluator", mockAuthorityEvaluator);
                return objects;
            }
        });
    }

    /**
     * checkAuthorityメソッドのテスト({@link AuthorityEvaluator}のメソッドと連携できていることを確認)。
     */
    @Test
    public void testCollaborateWithAuthorityEvaluatorWhenCallCheckAuthority() {
        AuthorityUtil.checkAuthority("FOO", context);

        assertThat(mockAuthorityEvaluator.calledMethodName, is("evaluateAllOf"));
        assertThat(mockAuthorityEvaluator.userId, is(ThreadContext.getUserId()));
        assertThat(mockAuthorityEvaluator.authorities, contains("FOO"));
        assertThat(mockAuthorityEvaluator.context, is(sameInstance(context)));
    }

    /**
     * checkAuthorityメソッドのテスト({@link AuthorityEvaluator}が返した結果をそのまま返却していること)。
     */
    @Test
    public void testReturnValueEqualToResultOfAuthorityEvaluatorWhenCallCheckAuthority() {
        mockAuthorityEvaluator.returnValue = true;
        assertThat(AuthorityUtil.checkAuthority("FOO", context), is(true));

        mockAuthorityEvaluator.returnValue = false;
        assertThat(AuthorityUtil.checkAuthority("FOO", context), is(false));
    }

    /**
     * checkAuthorityAllOfメソッドのテスト({@link AuthorityEvaluator}のメソッドと連携できていることを確認)。
     */
    @Test
    public void testCollaborateWithAuthorityEvaluatorWhenCallCheckAuthorityAllOf() {
        AuthorityUtil.checkAuthorityAllOf(Arrays.asList("FOO", "BAR"), context);

        assertThat(mockAuthorityEvaluator.calledMethodName, is("evaluateAllOf"));
        assertThat(mockAuthorityEvaluator.userId, is(ThreadContext.getUserId()));
        assertThat(mockAuthorityEvaluator.authorities, contains("FOO", "BAR"));
        assertThat(mockAuthorityEvaluator.context, is(sameInstance(context)));
    }

    /**
     * checkAuthorityAllOfメソッドのテスト({@link AuthorityEvaluator}が返した結果をそのまま返却していること)。
     */
    @Test
    public void testReturnValueEqualToResultOfAuthorityEvaluatorWhenCallCheckAuthorityAllOf() {
        mockAuthorityEvaluator.returnValue = true;
        assertThat(AuthorityUtil.checkAuthorityAllOf(Arrays.asList("FOO", "BAR"), context), is(true));

        mockAuthorityEvaluator.returnValue = false;
        assertThat(AuthorityUtil.checkAuthorityAllOf(Arrays.asList("FOO", "BAR"), context), is(false));
    }

    /**
     * checkAuthorityAnyOfメソッドのテスト({@link AuthorityEvaluator}のメソッドと連携できていることを確認)。
     */
    @Test
    public void testCollaborateWithAuthorityEvaluatorWhenCallCheckAuthorityAnyOf() {
        AuthorityUtil.checkAuthorityAnyOf(Arrays.asList("FOO", "BAR"), context);

        assertThat(mockAuthorityEvaluator.calledMethodName, is("evaluateAnyOf"));
        assertThat(mockAuthorityEvaluator.userId, is(ThreadContext.getUserId()));
        assertThat(mockAuthorityEvaluator.authorities, contains("FOO", "BAR"));
        assertThat(mockAuthorityEvaluator.context, is(sameInstance(context)));
    }

    /**
     * checkAuthorityAnyOfメソッドのテスト({@link AuthorityEvaluator}が返した結果をそのまま返却していること)。
     */
    @Test
    public void testReturnValueEqualToResultOfAuthorityEvaluatorWhenCallCheckAuthorityAnyOf() {
        mockAuthorityEvaluator.returnValue = true;
        assertThat(AuthorityUtil.checkAuthorityAnyOf(Arrays.asList("FOO", "BAR"), context), is(true));

        mockAuthorityEvaluator.returnValue = false;
        assertThat(AuthorityUtil.checkAuthorityAnyOf(Arrays.asList("FOO", "BAR"), context), is(false));
    }
}