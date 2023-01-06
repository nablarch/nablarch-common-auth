package nablarch.common.authorization;

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
 * {@link BasicAuthorityEvaluator}の単体テスト。
 *
 * @author Tanaka Tomoyuki
 */
public class BasicAuthorityEvaluatorTest {
    private static final String TEST_USER_ID = "test-user";

    private final BasicAuthorityEvaluator sut = new BasicAuthorityEvaluator();
    private final MockUserAuthorityResolver mockUserAuthorityResolver = new MockUserAuthorityResolver();
    private final ExecutionContext context = new ExecutionContext();

    @Before
    public void setUp() {
        mockUserAuthorityResolver.resolvedAuthorities = Collections.emptyList();
        sut.setUserAuthorityResolver(mockUserAuthorityResolver);
    }

    /**
     * allOfメソッドのテスト(ユーザIDと実行コンテキストが{@link UserAuthorityResolver}に渡せていること)。
     */
    @Test
    public void testPassUserIdAndContextToResolverWhenCallAllOf() {
        sut.evaluateAllOf(TEST_USER_ID, Arrays.asList("FOO", "BAR"), context);

        assertThat(mockUserAuthorityResolver.userId, is(TEST_USER_ID));
        assertThat(mockUserAuthorityResolver.context, is(sameInstance(context)));
    }

    /**
     * allOfメソッドのテスト(ユーザが指定された権限を全て有する場合はtrueを返すこと)。
     */
    @Test
    public void testReturnTrueIfUserHasAllAuthoritiesWhenCallAllOf() {
        mockUserAuthorityResolver.resolvedAuthorities = Arrays.asList("FIZZ", "FOO", "BAR", "BUZZ");

        boolean actual = sut.evaluateAllOf(TEST_USER_ID, Arrays.asList("FOO", "BAR"), context);

        assertThat(actual, is(true));
    }

    /**
     * allOfメソッドのテスト(ユーザが指定された権限の一部しか有していない場合はfalseを返すこと)。
     */
    @Test
    public void testReturnFalseIfUserOnlyHasPartOfAuthoritiesWhenCallOf() {
        mockUserAuthorityResolver.resolvedAuthorities = Arrays.asList("BAR", "FIZZ");

        boolean actual = sut.evaluateAllOf(TEST_USER_ID, Arrays.asList("FOO", "BAR"), context);

        assertThat(actual, is(false));
    }

    /**
     * allOfメソッドのテスト({@link UserAuthorityResolver}が設定されていない場合は例外をスローすること)。
     */
    @Test
    public void testThrowExceptionIfUserAuthorityResolverIsNullWhenCallAllOf() {
        sut.setUserAuthorityResolver(null);

        IllegalStateException exception = assertThrows(IllegalStateException.class, new ThrowingRunnable() {
            @Override
            public void run() {
                sut.evaluateAllOf(TEST_USER_ID, Arrays.asList("FOO", "BAR"), context);
            }
        });

        assertThat(exception.getMessage(), is("UserAuthorityResolver is null."));
    }

    /**
     * anyOfメソッドのテスト(ユーザIDと実行コンテキストが{@link UserAuthorityResolver}に渡せていること)。
     */
    @Test
    public void testPassUserIdAndContextToResolverWhenCallAnyOf() {
        sut.evaluateAnyOf(TEST_USER_ID, Arrays.asList("FOO", "BAR"), context);

        assertThat(mockUserAuthorityResolver.userId, is(TEST_USER_ID));
        assertThat(mockUserAuthorityResolver.context, is(sameInstance(context)));
    }

    /**
     * anyOfメソッドのテスト(ユーザが指定された権限のいずれか1つでも有する場合はtrueを返すこと)。
     */
    @Test
    public void testReturnTrueIfUserHasAnyOfAuthoritiesWhenCallAnyOf() {
        mockUserAuthorityResolver.resolvedAuthorities = Arrays.asList("FIZZ", "BAR", "BUZZ");

        boolean actual = sut.evaluateAnyOf(TEST_USER_ID, Arrays.asList("FOO", "BAR"), context);

        assertThat(actual, is(true));
    }

    /**
     * anyOfメソッドのテスト(ユーザが指定された権限をいずれも有していない場合はfalseを返すこと)。
     */
    @Test
    public void testReturnFalseIfUserOnlyHasNoAuthoritiesWhenCallAnyOf() {
        mockUserAuthorityResolver.resolvedAuthorities = Arrays.asList("FIZZ", "BUZZ");

        boolean actual = sut.evaluateAnyOf(TEST_USER_ID, Arrays.asList("FOO", "BAR"), context);

        assertThat(actual, is(false));
    }

    /**
     * anyOfメソッドのテスト({@link UserAuthorityResolver}が設定されていない場合は例外をスローすること)。
     */
    @Test
    public void testThrowExceptionIfUserAuthorityResolverIsNullWhenCallAnyOf() {
        sut.setUserAuthorityResolver(null);

        IllegalStateException exception = assertThrows(IllegalStateException.class, new ThrowingRunnable() {
            @Override
            public void run() {
                sut.evaluateAnyOf(TEST_USER_ID, Arrays.asList("FOO", "BAR"), context);
            }
        });

        assertThat(exception.getMessage(), is("UserAuthorityResolver is null."));
    }

    /**
     * {@link UserAuthorityResolver}のモック。
     */
    public static class MockUserAuthorityResolver implements UserAuthorityResolver {
        private String userId;
        private ExecutionContext context;
        private Collection<String> resolvedAuthorities;

        @Override
        public Collection<String> resolve(String userId, ExecutionContext context) {
            this.userId = userId;
            this.context = context;
            return resolvedAuthorities;
        }
    }
}