package nablarch.common.authorization.role;

import nablarch.core.ThreadContext;
import nablarch.core.repository.ObjectLoader;
import nablarch.core.repository.SystemRepository;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Handler;
import nablarch.fw.results.Forbidden;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThrows;

/**
 * {@link CheckAuthority.Impl}の単体テストクラス。
 *
 * @author Tanaka Tomoyuki
 */
public class CheckAuthorityImplTest {

    private final CheckAuthority.Impl sut = new CheckAuthority.Impl();
    private final MockAuthorityEvaluator mockAuthorityEvaluator = new MockAuthorityEvaluator();
    private final MockHandler mockHandler = new MockHandler();
    private final ExecutionContext context = new ExecutionContext();
    private final Object param = new Object();

    @Before
    public void setUp() {
        SystemRepository.clear();
        ThreadContext.clear();

        registerComponent("authorityEvaluator", mockAuthorityEvaluator);
        mockHandler.returnValue = "test";
        sut.setOriginalHandler(mockHandler);
        ThreadContext.setUserId("test-user");
    }

    /**
     * anyOfがfalseで認可の判定結果がtrueの場合に、以下を検証。
     * <ul>
     *   <li>{@link AuthorityEvaluator}にパラメータが意図通りに渡せていること</li>
     *   <li>{@link AuthorityEvaluator#evaluateAllOf(String, Collection, ExecutionContext)}が呼べていること</li>
     *   <li>後続ハンドラが実行されていること</li>
     * </ul>
     */
    @Test
    public void testAllOf() {
        class TestAction {
            @CheckAuthority({"FOO", "BAR"})
            public void method() {}
        }

        mockAuthorityEvaluator.returnValue = true;

        sut.setInterceptor(findAnnotation(TestAction.class));

        Object result = sut.handle(param, context);

        // AuthorityEvaluator の呼び出し確認
        assertThat(mockAuthorityEvaluator.calledMethodName, is("evaluateAllOf"));
        assertThat(mockAuthorityEvaluator.userId, is(ThreadContext.getUserId()));
        assertThat(mockAuthorityEvaluator.authorities, is(contains("FOO", "BAR")));
        assertThat(mockAuthorityEvaluator.context, is(sameInstance(context)));

        // 後続ハンドラの呼び出し確認
        assertThat(result, is(sameInstance(mockHandler.returnValue)));
        assertThat(mockHandler.param, is(sameInstance(param)));
        assertThat(mockHandler.context, is(sameInstance(context)));
    }

    /**
     * anyOfがtrueで認可の判定結果がtrueの場合に、以下を検証。
     * <ul>
     *   <li>{@link AuthorityEvaluator}にパラメータが意図通りに渡せていること</li>
     *   <li>{@link AuthorityEvaluator#evaluateAnyOf(String, Collection, ExecutionContext)}が呼べていること</li>
     *   <li>後続ハンドラが実行されていること</li>
     * </ul>
     */
    @Test
    public void testAnyOf() {
        class TestAction {
            @CheckAuthority(value = {"FOO", "BAR"}, anyOf = true)
            public void method() {}
        }

        mockAuthorityEvaluator.returnValue = true;

        sut.setInterceptor(findAnnotation(TestAction.class));

        Object result = sut.handle(param, context);

        // AuthorityEvaluator の呼び出し確認
        assertThat(mockAuthorityEvaluator.calledMethodName, is("evaluateAnyOf"));
        assertThat(mockAuthorityEvaluator.userId, is(ThreadContext.getUserId()));
        assertThat(mockAuthorityEvaluator.authorities, is(contains("FOO", "BAR")));
        assertThat(mockAuthorityEvaluator.context, is(sameInstance(context)));

        // 後続ハンドラの呼び出し確認
        assertThat(result, is(sameInstance(mockHandler.returnValue)));
        assertThat(mockHandler.param, is(sameInstance(param)));
        assertThat(mockHandler.context, is(sameInstance(context)));
    }

    /**
     * anyOfがfalseで認可の判定結果がfalseの場合に、以下を検証。
     * <ul>
     *   <li>後続ハンドラが実行されていないこと</li>
     *   <li>{@link nablarch.fw.results.Forbidden}がスローされること</li>
     * </ul>
     */
    @Test
    public void testAllOfWhenDenied() {
        class TestAction {
            @CheckAuthority({"FOO", "BAR"})
            public void method() {}
        }

        mockAuthorityEvaluator.returnValue = false;

        sut.setInterceptor(findAnnotation(TestAction.class));

        // 例外の確認
        Forbidden exception = assertThrows(Forbidden.class, new ThrowingRunnable() {
            @Override
            public void run() {
                sut.handle(param, context);
            }
        });
        assertThat(exception.getMessage(),
                is("User has no authority. userId=[test-user], authorities=[FOO, BAR]"));

        // 後続ハンドラが呼ばれていないことの確認
        assertThat(mockHandler.param, is(nullValue()));
    }

    /**
     * anyOfがtrueで認可の判定結果がfalseの場合に、以下を検証。
     * <ul>
     *   <li>後続ハンドラが実行されていないこと</li>
     *   <li>{@link nablarch.fw.results.Forbidden}がスローされること</li>
     * </ul>
     */
    @Test
    public void testAnyOfWhenDenied() {
        class TestAction {
            @CheckAuthority(value = {"FOO", "BAR"}, anyOf = true)
            public void method() {}
        }

        mockAuthorityEvaluator.returnValue = false;

        sut.setInterceptor(findAnnotation(TestAction.class));

        // 例外の確認
        Forbidden exception = assertThrows(Forbidden.class, new ThrowingRunnable() {
            @Override
            public void run() {
                sut.handle(param, context);
            }
        });
        assertThat(exception.getMessage(),
                is("User has no authority. userId=[test-user], authorities=[FOO, BAR]"));

        // 後続ハンドラが呼ばれていないことの確認
        assertThat(mockHandler.param, is(nullValue()));
    }

    /**
     * {@link AuthorityEvaluator}がシステムリポジトリに登録されていない場合は例外がスローされること。
     */
    @Test
    public void testThrownExceptionWhenAuthorityEvaluatorIsNotRegisteredInSystemRepository() {
        SystemRepository.clear();

        IllegalStateException exception = assertThrows(IllegalStateException.class, new ThrowingRunnable() {
            @Override
            public void run() {
                sut.handle(param, context);
            }
        });

        assertThat(exception.getMessage(),
                is("The component of \"authorityEvaluator\" is not found."));
    }

    /**
     * システムリポジトリに、指定した名前でコンポーネントを登録する。
     * @param name コンポーネントの名前
     * @param component コンポーネント
     */
    private void registerComponent(final String name, final Object component) {
        SystemRepository.load(new ObjectLoader() {
            @Override
            public Map<String, Object> load() {
                Map<String, Object> objects = new HashMap<String, Object>();
                objects.put(name, component);
                return objects;
            }
        });
    }

    /**
     * 指定されたクラスのメソッドの中から{@link CheckAuthority}アノテーションが設定されたメソッドを見つけ、
     * そのアノテーションを返す。
     * @param actionClass 検索対象のクラス
     * @return {@link CheckAuthority}
     */
    private CheckAuthority findAnnotation(Class<?> actionClass) {
        for (Method method : actionClass.getMethods()) {
            if (method.isAnnotationPresent(CheckAuthority.class)) {
                return method.getAnnotation(CheckAuthority.class);
            }
        }
        throw new RuntimeException("CheckAuthority annotation is not found.");
    }

    /**
     * {@link Handler}のモック。
     */
    public static class MockHandler implements Handler<Object, Object> {

        public Object returnValue;
        public Object param;
        public ExecutionContext context;

        @Override
        public Object handle(Object param, ExecutionContext context) {
            this.param = param;
            this.context = context;
            return returnValue;
        }
    }
}
