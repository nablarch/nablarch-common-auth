// MOVE: commonをモジュール分割したので、nablarch.common.handlerから移動
package nablarch.common.permission;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import nablarch.common.permission.Permission;
import nablarch.common.permission.PermissionFactory;
import nablarch.common.permission.PermissionUtil;
import nablarch.core.ThreadContext;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.util.Builder;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Handler;
import nablarch.fw.results.Forbidden;

/**
 * 認可判定を行うハンドラ。
 * <br>
 * <br>
 * このクラスを使用する場合は、下記プロパティを設定する。
 * <dl>
 * <dt>{@link #permissionFactory}
 * <dd>{@link Permission}を生成する{@link PermissionFactory}。必須。
 * <dt>{@link #ignoreRequestIds}
 * <dd>認可判定を行わないリクエストID。オプション。<br>
 *     複数指定する場合はカンマ区切り。
 * </dl>
 * 
 * @author Kiyohito Itoh
 */
public class PermissionCheckHandler implements Handler<Object, Object> {
    
    // ------------------------------------------------------------ structure
    /** {@link Permission}を生成する{@link PermissionFactory} */
    private PermissionFactory permissionFactory;
    
    /** 認可判定を行わないリクエストID */
    private Set<String> ignoreRequestIds = new HashSet<String>();
    
    /** サービス提供可否判定を行う際に内部リクエストIDを使用するかどうか */
    private boolean usesInternalRequestId = false;
        
    // ----------------------------------------- implementation of Handler API
    /**
     * リクエストIDを使用して認可判定を行う。<br>
     * <br>
     * 下記の順に処理を行う。
     * <ol>
     * <li>{@link nablarch.core.ThreadContext}からリクエストIDを取得し、認可判定の対象リクエストかをチェックする。<br>
     *     対象でない場合は、認可判定を行わずに次のハンドラに処理を委譲する。</li>
     * <li>{@link ThreadContext#getUserId()}からユーザIDを取得する。<br>
     *     ユーザIDが設定されていない場合は、認可判定を行わずに次のハンドラに処理を委譲する。</li>
     * <li>ユーザに紐付く認可情報を取得し、認可判定を行う。<br>
     *     認可判定に成功した場合は、{@link ThreadContext}に{@link Permission}を設定し、次のハンドラに処理を委譲する。<br>
     *     認可判定に失敗した場合は、指定されたリソースパスとステータスコードを使用して{@link Forbidden}をスローする。
     * </li>
     * </ol>
     * @param inputData 処理対象データ
     * @param context 実行コンテキスト
     * @return 処理結果
     * @throws Forbidden 認可判定に失敗した場合(nablarch.fw.Result$Forbidden)
     */
    public Object handle(Object inputData, ExecutionContext context) throws Forbidden {
        String requestId = usesInternalRequestId
                         ? ThreadContext.getInternalRequestId()
                         : ThreadContext.getRequestId();
                         
        if (ignoreRequestIds.contains(requestId)) {
            return context.handleNext(inputData);
        }
        
        String userId = ThreadContext.getUserId();
        Permission permission = permissionFactory.getPermission(userId);
        
        if (permission.permit(requestId)) {
            PermissionUtil.setPermission(permission);
            return context.handleNext(inputData);
        } else {
            String message = Builder.concat(
                "permission denied. userId = [", userId, "], "
              , "requestId = [", requestId, "]"
            );
            LOGGER.logInfo(message);
            throw new Forbidden(message);
        }
    }
    
    // ------------------------------------------------------------- accessors
    /**
     * {@link Permission}を生成する{@link PermissionFactory}を設定する。
     * @param permissionFactory {@link Permission}を生成する{@link PermissionFactory}
     * @return 自身のインスタンス
     */
    public PermissionCheckHandler setPermissionFactory(PermissionFactory permissionFactory) {
        this.permissionFactory = permissionFactory;
        return this;
    }
    
    /**
     * 認可判定を行わないリクエストIDを設定する。
     * @param requestIds 認可判定を行わないリクエストID
     * @return 自身のインスタンス
     */
    public PermissionCheckHandler setIgnoreRequestIds(String... requestIds) {
        this.ignoreRequestIds.addAll(Arrays.asList(requestIds));
        return this;
    }
    
    /**
     * 認可判定を内部リクエストIDを用いて行うか否かを設定する。
     * 
     * 明示的に設定しなかった場合のデフォルトは true (内部リクエストIDを使用する。)
     * 
     * @param usesInternal 内部リクエストIDを使用して判定を行う場合は true
     *                      常に外部から送信されたリクエストIDを使って判定を行う場合は false
     * @return このハンドラインスタンス自体
     */
    public PermissionCheckHandler setUsesInternalRequestId(boolean usesInternal) {
        usesInternalRequestId = usesInternal;
        return this;
    }
    
    /** ロガー */
    private static final Logger LOGGER = LoggerManager.get(PermissionCheckHandler.class);
}
