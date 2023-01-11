package nablarch.common.authorization.role;

import nablarch.common.authorization.role.CheckAuthorityLogger;
import nablarch.core.log.LoggerManager;
import nablarch.core.util.StringUtil;
import nablarch.test.support.log.app.OnMemoryLogWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

/**
 * {@link CheckAuthorityLogger}の単体テスト。
 *
 * @author Tanaka Tomoyuki
 */
public class CheckAuthorityLoggerTest {
    private static final String LINE_SEP = System.getProperty("line.separator");
    private final CheckAuthorityLogger sut = new CheckAuthorityLogger();

    @Before
    public void setUp() {
        LoggerManager.terminate();
        OnMemoryLogWriter.clear();
        sut.setTargetPackage("nablarch.common.authorization.role.action");
    }

    /**
     * デフォルト状態のテスト。
     * <p>
     * 指定されたパッケージ以下の Action で終わるクラスが対象となること。
     * </p>
     */
    @Test
    public void testStandard() {
        sut.initialize();

        List<String> logs = OnMemoryLogWriter.getMessages("writer.onMemory");
        String log = logs.get(logs.size() - 1);

        assertThat(log, is(
            "DEBUG CheckAuthority Annotation Settings" + LINE_SEP +
            format("class", "signature", "authority", "anyOf") + LINE_SEP +
            format(
                "nablarch.common.authorization.role.action.BarAction",
                "publicMethod()",
                "FOO",
                "false") + LINE_SEP +
            format(
                "nablarch.common.authorization.role.action.FooAction",
                "publicMethodNoAnnotation()",
                "",
                "") + LINE_SEP +
            format(
                "nablarch.common.authorization.role.action.FooAction",
                "publicMethodWithAnnotationAnyOfTrue()",
                "FOO",
                "true") + LINE_SEP +
            format(
                "nablarch.common.authorization.role.action.FooAction",
                "publicMethodWithAnnotationMultipleArg(java.lang.String, int, java.util.List)",
                "FOO",
                "false") + LINE_SEP +
            format(
                "nablarch.common.authorization.role.action.FooAction",
                "publicMethodWithAnnotationMultipleAuthorities()",
                "BAR",
                "false") + LINE_SEP +
            format(
                "nablarch.common.authorization.role.action.FooAction",
                "publicMethodWithAnnotationMultipleAuthorities()",
                "BUZZ",
                "false") + LINE_SEP +
            format(
                "nablarch.common.authorization.role.action.FooAction",
                "publicMethodWithAnnotationMultipleAuthorities()",
                "FIZZ",
                "false") + LINE_SEP +
            format(
                "nablarch.common.authorization.role.action.FooAction",
                "publicMethodWithAnnotationMultipleAuthorities()",
                "FOO",
                "false") + LINE_SEP +
            format(
                "nablarch.common.authorization.role.action.FooAction",
                "publicMethodWithAnnotationNoArgs()",
                "FOO",
                "false") + LINE_SEP +
            format(
                "nablarch.common.authorization.role.action.FooAction",
                "publicMethodWithAnnotationSingleArg(java.lang.String)",
                "FOO",
                "false") + LINE_SEP +
            format(
                "nablarch.common.authorization.role.action.ParentAction",
                "parentMethod()",
                "FOO",
                "false") + LINE_SEP +
            format(
                "nablarch.common.authorization.role.action.sub.FizzAction",
                "publicMethod()",
                "FOO",
                "false") + LINE_SEP
        ));
    }

    /**
     * 対象クラスを変更した場合のテスト。
     */
    @Test
    public void testSetTargetClassPattern() {
        sut.setTargetClassPattern("^.*Service$");

        sut.initialize();

        List<String> logs = OnMemoryLogWriter.getMessages("writer.onMemory");
        String log = logs.get(logs.size() - 1);

        assertThat(log, is(
            "DEBUG CheckAuthority Annotation Settings" + LINE_SEP +
            format("class", "signature", "authority", "anyOf") + LINE_SEP +
            format(
                "nablarch.common.authorization.role.action.FooService",
                "publicMethod()",
                "FOO",
                "false") + LINE_SEP +
            format(
                "nablarch.common.authorization.role.action.sub.FizzService",
                "publicMethod()",
                "FOO",
                "false") + LINE_SEP
        ));
    }

    /**
     * デバッグレベルでない場合、ログは出力されないことを確認。
     */
    @Test
    public void testNoActionWhenLogLevelIsNotDebug() {
        System.setProperty("nablarch.log.filePath", "classpath:nablarch/common/authorization/CheckAuthorityLoggerTest/log.properties");

        sut.initialize();

        List<String> logs = OnMemoryLogWriter.getMessages("writer.onMemory");

        assertThat(logs, is(empty()));
    }

    @After
    public void tearDown() {
        System.clearProperty("nablarch.log.filePath");
    }

    /**
     * 各要素をタブで連結して返す。
     * @param className クラス名
     * @param signature メソッドシグネチャ
     * @param authority 権限
     * @param anyOf anyOf
     * @return 各要素をタブで連結した結果
     */
    private String format(String className, String signature, String authority, String anyOf) {
        List<String> elements = Arrays.asList(className, signature, authority, anyOf);
        return StringUtil.join("\t", elements);
    }
}