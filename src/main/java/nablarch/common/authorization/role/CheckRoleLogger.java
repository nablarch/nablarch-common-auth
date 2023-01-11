package nablarch.common.authorization.role;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.repository.initialization.Initializable;
import nablarch.core.util.ClassTraversal.ClassHandler;
import nablarch.core.util.ResourcesUtil;
import nablarch.core.util.ResourcesUtil.Resources;
import nablarch.core.util.StringUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * アクションメソッドに設定された{@link CheckRole}アノテーションの情報をログに出力するロガー。
 * <p>
 * このクラスは、指定されたパッケージ以下に存在するクラスを走査して、
 * 各メソッドとそこに設定された{@link CheckRole}アノテーションの情報を抽出する。
 * そして、抽出した情報をログにデバッグレベルで出力する。<br>
 * これは、アクションメソッドへの{@link CheckRole}の設定が設計通りになっているかを
 * 確認することを目的とした機能となる。
 * </p>
 * <p>
 * このクラスは、まず{@code targetPackage}で指定されたパッケージ配下を再帰的に走査し、
 * 処理対象の候補となるクラスを発見する。
 * 次に、発見したクラスの完全修飾名が{@code targetClassPattern}で指定された正規表現に
 * 一致するかどうかを確認し、一致する場合は対象のクラスとして処理する。<br>
 * そして、処理対象となったクラスから、そのクラスで宣言された{@code public}メソッドの情報が抽出される
 * （親クラスで宣言されたメソッドは対象にならない）。
 * 抽出されたメソッドからは、以下の情報が取り出されてログに出力される。
 * </p>
 * <ul>
 *   <li>クラスの完全修飾名({@code Class.getName()}で取得できる値)</li>
 *   <li>シグネチャ(メソッド名と引数の型の並び)</li>
 *   <li>{@link CheckRole}アノテーションの {@code value} に設定された値(未設定の場合は空)</li>
 *   <li>{@link CheckRole}アノテーションの {@code anyOf} に設定された値(未設定の場合は空)</li>
 * </ul>
 * <p>
 * このクラスは{@link Initializable}を実装しており、アプリケーション起動時の初期化のタイミングで
 * ログを出力する。
 * </p>
 *
 * @author Tanaka Tomoyuki
 */
public class CheckRoleLogger implements Initializable {
    /**
     * 行の区切り文字。
     */
    private static final String LINE_SEP = System.getProperty("line.separator");
    /**
     * 要素の区切り文字。
     */
    private static final String SEP = "\t";
    /**
     * ログのタイトル。
     */
    private static final String TITLE = "CheckRole Annotation Settings";
    /**
     * ログのヘッダー。
     */
    private static final String HEADER = StringUtil.join(SEP, Arrays.asList("class", "signature", "role", "anyOf"));

    private String targetPackage;
    private String targetClassPattern = "^.*Action$";

    @Override
    public void initialize() {
        // Logger を static 変数にしていると、単体テストでログレベルを切り替えるテストができないのでローカルで取得している。
        // 初期化時に一度しか実行されない処理なので、 static 変数にした場合と差は無い。
        final Logger logger = LoggerManager.get(CheckRoleLogger.class);

        if (!logger.isDebugEnabled()) {
            return;
        }

        final List<Method> targetMethods = findTargetMethods();
        final List<String> formattedSettings = formatMethodSettings(targetMethods);

        logger.logDebug( TITLE + LINE_SEP +
                HEADER + LINE_SEP +
                StringUtil.join(LINE_SEP, formattedSettings));
    }

    /**
     * 出力対象のメソッドを抽出する。
     * @return 出力対象のメソッド一覧
     */
    private List<Method> findTargetMethods() {
        final TargetMethodFinder targetMethodFinder = new TargetMethodFinder(targetClassPattern);

        for (Resources resourcesType : ResourcesUtil.getResourcesTypes(targetPackage)) {
            try {
                resourcesType.forEach(targetMethodFinder);
            } finally {
                resourcesType.close();
            }
        }

        return targetMethodFinder.getTargetMethods();
    }

    /**
     * 各メソッドに設定された{@link CheckRole}の情報をログ出力用にフォーマットする。
     * @param targetMethods 出力対象のメソッド一覧
     * @return 各メソッドの設定をフォーマットしたログメッセージ一覧
     */
    private List<String> formatMethodSettings(List<Method> targetMethods) {
        final List<String> formattedSettings = new ArrayList<String>();

        for (Method method : targetMethods) {
            final CheckRole checkRole = method.getAnnotation(CheckRole.class);

            if (checkRole == null) {
                final AnnotationSettings settings = new AnnotationSettings(method);
                formattedSettings.add(settings.format());
            } else {
                for (String role : checkRole.value()) {
                    final AnnotationSettings settings
                        = new AnnotationSettings(method, role, checkRole.anyOf());
                    formattedSettings.add(settings.format());
                }
            }
        }

        Collections.sort(formattedSettings);

        return formattedSettings;
    }

    /**
     * 1行に出力する{@link CheckRole}の設定情報を保持し、フォーマットを行うためのクラス。
     */
    private static class AnnotationSettings {
        private final Method method;
        private final String role;
        private final String anyOf;

        /**
         * {@link CheckRole}が設定されていないメソッド用のコンストラクタ。
         * @param method 対象のメソッド
         */
        private AnnotationSettings(Method method) {
            this.method = method;
            this.role = "";
            this.anyOf = "";
        }

        /**
         * {@link CheckRole}が設定されているメソッド用のコンストラクタ。
         * @param method 対象のメソッド
         * @param role {@link CheckRole}に設定されていたロールの1つ
         * @param anyOf {@link CheckRole}の{@code anyOf}
         */
        private AnnotationSettings(Method method, String role, boolean anyOf) {
            this.method = method;
            this.role = role;
            this.anyOf = Boolean.toString(anyOf);
        }

        /**
         * 出力用にフォーマットした文字列を返す。
         * @return 出力用にフォーマットした文字列
         */
        private String format() {
            final List<String> settings =
                Arrays.asList(formatClassName(), formatMethodSignature(), role, anyOf);
            return StringUtil.join(SEP, settings);
        }

        /**
         * クラス名をログ出力用にフォーマットする。
         * @return ログ出力用にフォーマットされたクラス名
         */
        private String formatClassName() {
            return method.getDeclaringClass().getName();
        }

        /**
         * メソッドシグネチャをログ出力用にフォーマットする。
         * @return ログ出力用にフォーマットされたメソッドシグネチャ
         */
        private String formatMethodSignature() {
            final List<String> parameterTypeNames = new ArrayList<String>();
            for (Class<?> parameterType : method.getParameterTypes()) {
                parameterTypeNames.add(parameterType.getName());
            }
            return method.getName() + "(" + StringUtil.join(", ", parameterTypeNames) + ")";
        }
    }

    /**
     * 処理対象のメソッドを抽出するための{@link ClassHandler}実装。
     */
    private static class TargetMethodFinder implements ClassHandler {
        private final Pattern targetPackagePattern;
        private final ClassLoader classLoader = this.getClass().getClassLoader();
        private final Set<Class<?>> targetClasses = new HashSet<Class<?>>();

        /**
         * コンストラクタ。
         * @param targetClassPattern 検索対象のパッケージ
         */
        private TargetMethodFinder(String targetClassPattern) {
            this.targetPackagePattern = Pattern.compile(targetClassPattern);
        }

        @Override
        public void process(String packageName, String className) {
            final String fqcn = packageName + "." + className;
            if (!targetPackagePattern.matcher(fqcn).matches()) {
                return;
            }

            try {
                final Class<?> clazz = classLoader.loadClass(fqcn);
                targetClasses.add(clazz);
            } catch (ClassNotFoundException e) {
                // クラスパスを実際に検索した結果を用いているため、
                // ここでクラスが見つからないということはあり得ない
                throw new RuntimeException(e);
            }
        }

        /**
         * 処理対象のメソッドの一覧を取得する。
         * @return 処理対象のメソッドの一覧
         */
        private List<Method> getTargetMethods() {
            final List<Method> methods = new ArrayList<Method>();
            for (Class<?> targetClass : targetClasses) {
                for (Method method : targetClass.getDeclaredMethods()) {
                    if (isTargetMethod(method)) {
                        methods.add(method);
                    }
                }
            }
            return methods;
        }

        /**
         * 指定されたメソッドが、ログ出力の対象となるか確認する。
         * @param method 検査対象のメソッド
         * @return ログ出力対象の場合は {@code true}
         */
        private boolean isTargetMethod(Method method) {
            return Modifier.isPublic(method.getModifiers())
                    && !Modifier.isStatic(method.getModifiers());
        }
    }

    /**
     * 走査対象となるパッケージの名前を設定する。
     * @param targetPackage 走査対象となるパッケージの名前
     */
    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    /**
     * 処理対象となるクラスを特定するための正規表現を設定する。
     * <p>
     * この正規表現は、クラスの完全修飾名に対して適用される。<br>
     * デフォルトは {@code ^.*Action$} が設定されている({@code "Action"}で終わるクラスが対象)。
     * </p>
     * @param targetClassPattern 処理対象となるクラスを特定するための正規表現
     */
    public void setTargetClassPattern(String targetClassPattern) {
        this.targetClassPattern = targetClassPattern;
    }
}
