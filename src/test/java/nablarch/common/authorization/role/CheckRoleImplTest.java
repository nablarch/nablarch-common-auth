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
 * {@link CheckRole.Impl}の単体テストクラス。
 *
 * @author Tanaka Tomoyuki
 */
public class CheckRoleImplTest {

    private final CheckRole.Impl sut = new CheckRole.Impl();
    private final MockRoleEvaluator mockRoleEvaluator = new MockRoleEvaluator();
    private final MockHandler mockHandler = new MockHandler();
    private final ExecutionContext context = new ExecutionContext();
    private final Object param = new Object();

    @Before
    public void setUp() {
        SystemRepository.clear();
        ThreadContext.clear();

        registerComponent("roleEvaluator", mockRoleEvaluator);
        mockHandler.returnValue = "test";
        sut.setOriginalHandler(mockHandler);
        ThreadContext.setUserId("test-user");
    }

    /**
     * anyOfがfalseで認可の判定結果がtrueの場合に、以下を検証。
     * <ul>
     *   <li>{@link RoleEvaluator}にパラメータが意図通りに渡せていること</li>
     *   <li>{@link RoleEvaluator#evaluateAllOf(String, Collection, ExecutionContext)}が呼べていること</li>
     *   <li>後続ハンドラが実行されていること</li>
     * </ul>
     */
    @Test
    public void testAllOf() {
        class TestAction {
            @CheckRole({"FOO", "BAR"})
            public void method() {}
        }

        mockRoleEvaluator.returnValue = true;

        sut.setInterceptor(findAnnotation(TestAction.class));

        Object result = sut.handle(param, context);

        // RoleEvaluator の呼び出し確認
        assertThat(mockRoleEvaluator.calledMethodName, is("evaluateAllOf"));
        assertThat(mockRoleEvaluator.userId, is(ThreadContext.getUserId()));
        assertThat(mockRoleEvaluator.roles, is(contains("FOO", "BAR")));
        assertThat(mockRoleEvaluator.context, is(sameInstance(context)));

        // 後続ハンドラの呼び出し確認
        assertThat(result, is(sameInstance(mockHandler.returnValue)));
        assertThat(mockHandler.param, is(sameInstance(param)));
        assertThat(mockHandler.context, is(sameInstance(context)));
    }

    /**
     * anyOfがtrueで認可の判定結果がtrueの場合に、以下を検証。
     * <ul>
     *   <li>{@link RoleEvaluator}にパラメータが意図通りに渡せていること</li>
     *   <li>{@link RoleEvaluator#evaluateAnyOf(String, Collection, ExecutionContext)}が呼べていること</li>
     *   <li>後続ハンドラが実行されていること</li>
     * </ul>
     */
    @Test
    public void testAnyOf() {
        class TestAction {
            @CheckRole(value = {"FOO", "BAR"}, anyOf = true)
            public void method() {}
        }

        mockRoleEvaluator.returnValue = true;

        sut.setInterceptor(findAnnotation(TestAction.class));

        Object result = sut.handle(param, context);

        // RoleEvaluator の呼び出し確認
        assertThat(mockRoleEvaluator.calledMethodName, is("evaluateAnyOf"));
        assertThat(mockRoleEvaluator.userId, is(ThreadContext.getUserId()));
        assertThat(mockRoleEvaluator.roles, is(contains("FOO", "BAR")));
        assertThat(mockRoleEvaluator.context, is(sameInstance(context)));

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
            @CheckRole({"FOO", "BAR"})
            public void method() {}
        }

        mockRoleEvaluator.returnValue = false;

        sut.setInterceptor(findAnnotation(TestAction.class));

        // 例外の確認
        Forbidden exception = assertThrows(Forbidden.class, new ThrowingRunnable() {
            @Override
            public void run() {
                sut.handle(param, context);
            }
        });
        assertThat(exception.getMessage(),
                is("User has no role. userId=[test-user], roles=[FOO, BAR]"));

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
            @CheckRole(value = {"FOO", "BAR"}, anyOf = true)
            public void method() {}
        }

        mockRoleEvaluator.returnValue = false;

        sut.setInterceptor(findAnnotation(TestAction.class));

        // 例外の確認
        Forbidden exception = assertThrows(Forbidden.class, new ThrowingRunnable() {
            @Override
            public void run() {
                sut.handle(param, context);
            }
        });
        assertThat(exception.getMessage(),
                is("User has no role. userId=[test-user], roles=[FOO, BAR]"));

        // 後続ハンドラが呼ばれていないことの確認
        assertThat(mockHandler.param, is(nullValue()));
    }

    /**
     * {@link RoleEvaluator}がシステムリポジトリに登録されていない場合は例外がスローされること。
     */
    @Test
    public void testThrownExceptionWhenRoleEvaluatorIsNotRegisteredInSystemRepository() {
        SystemRepository.clear();

        IllegalStateException exception = assertThrows(IllegalStateException.class, new ThrowingRunnable() {
            @Override
            public void run() {
                sut.handle(param, context);
            }
        });

        assertThat(exception.getMessage(),
                is("The component of \"roleEvaluator\" is not found."));
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
     * 指定されたクラスのメソッドの中から{@link CheckRole}アノテーションが設定されたメソッドを見つけ、
     * そのアノテーションを返す。
     * @param actionClass 検索対象のクラス
     * @return {@link CheckRole}
     */
    private CheckRole findAnnotation(Class<?> actionClass) {
        for (Method method : actionClass.getMethods()) {
            if (method.isAnnotationPresent(CheckRole.class)) {
                return method.getAnnotation(CheckRole.class);
            }
        }
        throw new RuntimeException("CheckRole annotation is not found.");
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
