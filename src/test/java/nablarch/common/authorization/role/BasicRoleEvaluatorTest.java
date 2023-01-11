package nablarch.common.authorization.role;

import nablarch.fw.ExecutionContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

/**
 * {@link BasicRoleEvaluator}の単体テスト。
 *
 * @author Tanaka Tomoyuki
 */
public class BasicRoleEvaluatorTest {
    private static final String TEST_USER_ID = "test-user";

    private final BasicRoleEvaluator sut = new BasicRoleEvaluator();
    private final MockUserRoleResolver mockUserRoleResolver = new MockUserRoleResolver();
    private final ExecutionContext context = new ExecutionContext();

    @Before
    public void setUp() {
        mockUserRoleResolver.resolvedRoles = Collections.emptyList();
        sut.setUserRoleResolver(mockUserRoleResolver);
    }

    /**
     * allOfメソッドのテスト(ユーザIDと実行コンテキストが{@link UserRoleResolver}に渡せていること)。
     */
    @Test
    public void testPassUserIdAndContextToResolverWhenCallAllOf() {
        sut.evaluateAllOf(TEST_USER_ID, Arrays.asList("FOO", "BAR"), context);

        assertThat(mockUserRoleResolver.userId, is(TEST_USER_ID));
        assertThat(mockUserRoleResolver.context, is(sameInstance(context)));
    }

    /**
     * allOfメソッドのテスト(ユーザが指定されたロールを全て有する場合はtrueを返すこと)。
     */
    @Test
    public void testReturnTrueIfUserHasAllRolesWhenCallAllOf() {
        mockUserRoleResolver.resolvedRoles = Arrays.asList("FIZZ", "FOO", "BAR", "BUZZ");

        boolean actual = sut.evaluateAllOf(TEST_USER_ID, Arrays.asList("FOO", "BAR"), context);

        assertThat(actual, is(true));
    }

    /**
     * allOfメソッドのテスト(ユーザが指定されたロールの一部しか有していない場合はfalseを返すこと)。
     */
    @Test
    public void testReturnFalseIfUserOnlyHasPartOfRolesWhenCallOf() {
        mockUserRoleResolver.resolvedRoles = Arrays.asList("BAR", "FIZZ");

        boolean actual = sut.evaluateAllOf(TEST_USER_ID, Arrays.asList("FOO", "BAR"), context);

        assertThat(actual, is(false));
    }

    /**
     * allOfメソッドのテスト({@link UserRoleResolver}が設定されていない場合は例外をスローすること)。
     */
    @Test
    public void testThrowExceptionIfUserRoleResolverIsNullWhenCallAllOf() {
        sut.setUserRoleResolver(null);

        IllegalStateException exception = assertThrows(IllegalStateException.class, new ThrowingRunnable() {
            @Override
            public void run() {
                sut.evaluateAllOf(TEST_USER_ID, Arrays.asList("FOO", "BAR"), context);
            }
        });

        assertThat(exception.getMessage(), is("UserRoleResolver is null."));
    }

    /**
     * anyOfメソッドのテスト(ユーザIDと実行コンテキストが{@link UserRoleResolver}に渡せていること)。
     */
    @Test
    public void testPassUserIdAndContextToResolverWhenCallAnyOf() {
        sut.evaluateAnyOf(TEST_USER_ID, Arrays.asList("FOO", "BAR"), context);

        assertThat(mockUserRoleResolver.userId, is(TEST_USER_ID));
        assertThat(mockUserRoleResolver.context, is(sameInstance(context)));
    }

    /**
     * anyOfメソッドのテスト(ユーザが指定されたロールのいずれか1つでも有する場合はtrueを返すこと)。
     */
    @Test
    public void testReturnTrueIfUserHasAnyOfRolesWhenCallAnyOf() {
        mockUserRoleResolver.resolvedRoles = Arrays.asList("FIZZ", "BAR", "BUZZ");

        boolean actual = sut.evaluateAnyOf(TEST_USER_ID, Arrays.asList("FOO", "BAR"), context);

        assertThat(actual, is(true));
    }

    /**
     * anyOfメソッドのテスト(ユーザが指定されたロールをいずれも有していない場合はfalseを返すこと)。
     */
    @Test
    public void testReturnFalseIfUserOnlyHasNoRolesWhenCallAnyOf() {
        mockUserRoleResolver.resolvedRoles = Arrays.asList("FIZZ", "BUZZ");

        boolean actual = sut.evaluateAnyOf(TEST_USER_ID, Arrays.asList("FOO", "BAR"), context);

        assertThat(actual, is(false));
    }

    /**
     * anyOfメソッドのテスト({@link UserRoleResolver}が設定されていない場合は例外をスローすること)。
     */
    @Test
    public void testThrowExceptionIfUserRoleResolverIsNullWhenCallAnyOf() {
        sut.setUserRoleResolver(null);

        IllegalStateException exception = assertThrows(IllegalStateException.class, new ThrowingRunnable() {
            @Override
            public void run() {
                sut.evaluateAnyOf(TEST_USER_ID, Arrays.asList("FOO", "BAR"), context);
            }
        });

        assertThat(exception.getMessage(), is("UserRoleResolver is null."));
    }

    /**
     * {@link UserRoleResolver}のモック。
     */
    public static class MockUserRoleResolver implements UserRoleResolver {
        private String userId;
        private ExecutionContext context;
        private Collection<String> resolvedRoles;

        @Override
        public Collection<String> resolve(String userId, ExecutionContext context) {
            this.userId = userId;
            this.context = context;
            return resolvedRoles;
        }
    }
}