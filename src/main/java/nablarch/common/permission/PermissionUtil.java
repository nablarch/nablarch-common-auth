package nablarch.common.permission;

import nablarch.core.ThreadContext;
import nablarch.core.util.annotation.Published;

/**
 * 権限管理に使用するユーティリティクラス。
 * 
 * @author Koichi Asano
 *
 */
public final class PermissionUtil {
    
    /**
     * 隠蔽コンストラクタ。
     */
    private PermissionUtil() {
        
    }

    /** Permissionのキー */
    private static final String PERMISSION_KEY = "PERMISSION";

    /**
     * {@link ThreadContext}から{@link Permission}を取得する。
     * <p/>
     * ThreadContextにPermissionが設定されていない場合は{@code null}を返却する。
     *
     * @return 取得したPermissionオブジェクト
     */
    @Published
    public static Permission getPermission() {
        return (Permission) ThreadContext.getObject(PERMISSION_KEY);
    }

    /**
     * {@link ThreadContext}に{@link Permission}を設定する。
     * <p/>
     * 既にThreadContextにPermissionが登録されている場合は上書きする。
     *
     * @param permission 設定するPermissionオブジェクト
     */
    @Published(tag = "architect")
    public static void setPermission(Permission permission) {
        ThreadContext.setObject(PERMISSION_KEY, permission);
    }

}
