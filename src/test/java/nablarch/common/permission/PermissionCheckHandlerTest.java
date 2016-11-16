package nablarch.common.permission;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.SortedSet;
import java.util.TreeSet;

import nablarch.core.ThreadContext;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Handler;
import nablarch.fw.results.Forbidden;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link PermissionCheckHandler}のテストクラス。
 *
 * @author Kiyohito Itoh
 */
public class PermissionCheckHandlerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * 認可判定を行わないリクエストIDの場合。
     */
    @Test
    public void testHandleForIgnoreRequest() {

        ExecutionContext context = new ExecutionContext()
                .addHandler(
                        new PermissionCheckHandler()
                                .setIgnoreRequestIds("aaa")
                                .setUsesInternalRequestId(true)     // 内部リクエストIDを使用する
                )
                .addHandler(new Handler<Object, Object>() {
                    @Override
                    public Object handle(final Object o, final ExecutionContext context) {
                        return "ok";
                    }
                });

        ThreadContext.setUserId(null);
        ThreadContext.setInternalRequestId("aaa");

        final String res = context.handleNext("param");
        assertThat(res, is("ok"));
    }


    /**
     * リクエストIDが取得できない場合。
     */
    @Test
    public void testHandleForRequestIdIsNull() {
        ExecutionContext context = new ExecutionContext()
                .addHandler(
                        new PermissionCheckHandler()
                                .setPermissionFactory(
                                        new FixedPermissionFactory(new TreeSet<String>())
                                )
                )
                .addHandler(
                        new Handler<Object, Object>() {
                            @Override
                            public Object handle(final Object o, final ExecutionContext context) {
                                return "ok";
                            }
                        }
                );


        ThreadContext.setUserId("dummy");
        ThreadContext.setRequestId("aaa");

        expectedException.expect(Forbidden.class);
        context.handleNext("param");

    }

    /**
     * {@link PermissionCheckHandler#handle(nablarch.fw.web.HttpRequest, nablarch.fw.ExecutionContext)}のテスト。<br>
     * 認可に失敗した場合。
     */
    @Test
    public void testHandleForPermissionFailed() {
        ExecutionContext context = new ExecutionContext()
                .addHandler(
                        new PermissionCheckHandler()
                                .setPermissionFactory(new FixedPermissionFactory(new TreeSet<String>()))
                )
                .addHandler(new Handler<Object, Object>() {
                    @Override
                    public Object handle(final Object o, final ExecutionContext context) {
                        return "ok";
                    }
                });

        ThreadContext.setUserId("dummy");
        ThreadContext.setInternalRequestId("aaa");

        expectedException.expect(Forbidden.class);
        context.handleNext("param");
    }

    /**
     * 認可に成功した場合。
     */
    @Test
    public void testHandleForPermissionSuccess() {
        ExecutionContext context = new ExecutionContext()
                .addHandler(new PermissionCheckHandler()
                        .setIgnoreRequestIds("unknown")
                        .setUsesInternalRequestId(true)
                        .setPermissionFactory(
                                new FixedPermissionFactory(
                                        new TreeSet<String>() {{
                                            add("aaa");
                                        }}
                                )
                        )
                )
                .addHandler(
                        new Handler<Object, Object>() {
                            @Override
                            public Object handle(final Object o, final ExecutionContext context) {
                                return "ok";
                            }
                        }
                );

        ThreadContext.setUserId("dummy");
        ThreadContext.setInternalRequestId("aaa");

        final String res = context.handleNext("param");
        assertThat(res, is("ok"));

        final Permission permission = PermissionUtil.getPermission();
        assertThat(permission.permit("aaa"), is(true));
        assertThat(permission.permit("aab"), is(false));
    }

    private static class FixedPermissionFactory implements PermissionFactory {

        private Permission permission;

        public FixedPermissionFactory(SortedSet<String> requestIds) {
            permission = new BasicPermission(requestIds);
        }

        public Permission getPermission(String userId) {
            return permission;
        }
    }
}
