package nablarch.common.permission;

import java.util.SortedSet;

/**
 * {@link Permission}の基本実装クラス。<br>
 * <br>
 * ユーザに許可されたリクエストIDを保持しておき認可判定に使用する。
 * 
 * @author Kiyohito Itoh
 */
public class BasicPermission implements Permission {
    
    /**
     * ユーザに許可されたリクエストID
     */
    private SortedSet<String> requestIds;
    
    /**
     * デフォルトコンストラクタ。
     * @param requestIds ユーザに許可されたリクエストID
     */
    public BasicPermission(SortedSet<String> requestIds) {
        this.requestIds = requestIds;
    }

    /**
     * リクエストIDを認可判定する。
     * 
     * @param requestId リクエストID
     * @return 認可に成功した場合は<code>true</code>、認可に失敗した場合は<code>false</code>
     */
    public boolean permit(String requestId) {
        return requestIds != null && requestId != null && requestIds.contains(requestId);
    }
    
    /**
     * ユーザに許可されたリクエストIDを取得する。
     * 
     * @return ユーザに許可されたリクエストID
     */
    public SortedSet<String> getRequestIds() {
        return requestIds;
    }
}
