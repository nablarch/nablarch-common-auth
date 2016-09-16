package nablarch.common.availability;

import nablarch.core.util.annotation.Published;

/**
 * サービス提供可否状態を判定するインタフェース。
 * 
 * @author Masayuki Fujikuma
 */
@Published(tag = "architect")
public interface ServiceAvailability {

    /**
     * パラメータのリクエストIDを元に、サービス提供可否状態を判定し結果を返却する。
     * 
     * @param requestId リクエストID
     * @return サービス提供可否状態を表すboolean （提供可の場合、TRUE）
     */
    boolean isAvailable(String requestId);

}
