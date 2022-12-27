package nablarch.common.authorization;

import nablarch.core.ThreadContext;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.StringUtil;
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
 * アクションのメソッドを実行する権限があるかチェックする{@link Interceptor}。
 * <p>
 * このアノテーションをアクションのメソッドに設定することで、
 * アクセスしてきたユーザにそのメソッドを実行する権限があるかどうかを
 * チェックできるようになる。<br>
 * アクセスしてきたユーザの識別子は、{@link ThreadContext#getUserId() ThreadContextのgetUserIdメソッド}で
 * 取得できるものが利用される。<br>
 * また、権限の有無の判定は{@link AuthorityEvaluator}に委譲して行われる。
 * この{@link AuthorityEvaluator}のインスタンスは、システムリポジトリから{@code "authorityEvaluator"}という名前で
 * 取得したものを使用する。
 * </p>
 * <p>
 * 判定の結果権限がないと判断された場合は、{@link Forbidden}がスローされる。
 * </p>
 * @author Tanaka Tomoyuki
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Interceptor(CheckAuthority.Impl.class)
@Documented
public @interface CheckAuthority {

    /**
     * 判定の条件となる権限の一覧。
     * @return 権限の一覧
     */
    String[] value();

    /**
     * 権限の一覧による判定を部分一致とするかどうかのフラグ。
     * <p>
     * {@code ture}の場合、権限の一覧で指定したもののうちどれか1つでも
     * 権限を有していれば許可と判定される。<br>
     * {@code false}の場合は、権限の一覧で指定されたものを全て有している場合に
     * 許可と判定される。
     * </p>
     * <p>
     * デフォルトは{@code false}。
     * </p>
     * @return 権限の一覧を部分一致で判定する場合は {@code true}
     */
    boolean anyOf() default false;

    /**
     * {@link CheckAuthority}アノテーションのインターセプタ。
     *
     * @author Tanaka Tomoyuki
     */
    class Impl extends Interceptor.Impl<Object, Object, CheckAuthority> {

        @Override
        public Object handle(Object param, ExecutionContext context) {
            final AuthorityEvaluator authorityEvaluator = SystemRepository.get("authorityEvaluator");
            if (authorityEvaluator == null) {
                throw new IllegalStateException("The component of \"authorityEvaluator\" is not found.");
            }

            final CheckAuthority checkAuthority = getInterceptor();
            final String userId = ThreadContext.getUserId();
            final List<String> authorities = Arrays.asList(checkAuthority.value());

            boolean authorized;
            if (checkAuthority.anyOf()) {
                authorized = authorityEvaluator.evaluateAnyOf(userId, authorities, context);
            } else {
                authorized = authorityEvaluator.evaluateAllOf(userId, authorities, context);
            }

            if (!authorized) {
                throw new Forbidden("User has no authority. userId=[" + userId + "], " +
                        "authorities=[" + StringUtil.join(", ", authorities) + "]");
            }

            return getOriginalHandler().handle(param, context);
        }
    }
}
