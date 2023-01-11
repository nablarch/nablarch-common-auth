package nablarch.common.authorization.role;

import nablarch.core.ThreadContext;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.StringUtil;
import nablarch.core.util.annotation.Published;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Interceptor;
import nablarch.fw.results.Forbidden;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

/**
 * アクションのメソッドを実行するために必要なロールを、
 * アクセスしてきたユーザが持つかチェックする{@link Interceptor}。
 * <p>
 * このアノテーションをアクションのメソッドに設定することで、
 * そのメソッドを実行するために必要なロールをアクセスしてきたユーザが持つかどうかを
 * チェックできるようになる。<br>
 * アクセスしてきたユーザの識別子は、{@link ThreadContext#getUserId() ThreadContextのgetUserIdメソッド}で
 * 取得できるものが利用される。<br>
 * また、ロールの有無の判定は{@link RoleEvaluator}に委譲して行われる。
 * この{@link RoleEvaluator}のインスタンスは、システムリポジトリから{@code "roleEvaluator"}という名前で
 * 取得したものを使用する。
 * </p>
 * <p>
 * 判定の結果ロールを持たないと判断された場合は、{@link Forbidden}がスローされる。
 * </p>
 * @author Tanaka Tomoyuki
 */
@Published
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Interceptor(CheckRole.Impl.class)
@Documented
public @interface CheckRole {

    /**
     * 判定の条件となるロールの一覧。
     * @return ロールの一覧
     */
    String[] value();

    /**
     * ロールの一覧による判定を部分一致とするかどうかのフラグ。
     * <p>
     * {@code ture}の場合、ロールの一覧で指定したもののうちどれか1つでも
     * ロールを有していれば許可と判定される。<br>
     * {@code false}の場合は、ロールの一覧で指定されたものを全て有している場合に
     * 許可と判定される。
     * </p>
     * <p>
     * デフォルトは{@code false}。
     * </p>
     * @return ロールの一覧を部分一致で判定する場合は {@code true}
     */
    boolean anyOf() default false;

    /**
     * {@link CheckRole}アノテーションのインターセプタ。
     *
     * @author Tanaka Tomoyuki
     */
    class Impl extends Interceptor.Impl<Object, Object, CheckRole> {

        @Override
        public Object handle(Object param, ExecutionContext context) {
            final RoleEvaluator roleEvaluator = SystemRepository.get("roleEvaluator");
            if (roleEvaluator == null) {
                throw new IllegalStateException("The component of \"roleEvaluator\" is not found.");
            }

            final CheckRole checkRole = getInterceptor();
            final String userId = ThreadContext.getUserId();
            final List<String> roles = Arrays.asList(checkRole.value());

            boolean authorized;
            if (checkRole.anyOf()) {
                authorized = roleEvaluator.evaluateAnyOf(userId, roles, context);
            } else {
                authorized = roleEvaluator.evaluateAllOf(userId, roles, context);
            }

            if (!authorized) {
                throw new Forbidden("User has no role. userId=[" + userId + "], " +
                        "roles=[" + StringUtil.join(", ", roles) + "]");
            }

            return getOriginalHandler().handle(param, context);
        }
    }
}
