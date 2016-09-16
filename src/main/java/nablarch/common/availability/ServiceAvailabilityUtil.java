package nablarch.common.availability;

import nablarch.core.repository.SystemRepository;
import nablarch.core.util.annotation.Published;

/**
 * サービス提供可否状態を判定するユーティリティ。
 * <p/>
 *  サービス提供可否状態を判定する処理は{@link ServiceAvailability}によって提供される。
 * {@link ServiceAvailability}の実装は、{@link nablarch.core.repository.SystemRepository}からコンポーネント名 serviceAvailability で取得される。
 *
 * @author Masato Inoue
 * @see nablarch.common.availability.ServiceAvailability
 */
@Published(tag = "architect")
public final class ServiceAvailabilityUtil {
    
    /**
     * リポジトリ上のServiceAvailabilityの名称。
     */
    private static final String SERVICE_AVAILABILITY_NAME = "serviceAvailability";

    /**
     * プライベートコンストラクタ。
     */
    private ServiceAvailabilityUtil() {
    }

    /**
     * システムリポジトリからServiceAvailabilityを取得する。
     * @return システムリポジトリから取得したServiceAvailability
     */
    private static ServiceAvailability getServiceAvailability() {
        ServiceAvailability availability = (ServiceAvailability) SystemRepository.get(SERVICE_AVAILABILITY_NAME);
        if(availability == null){
            throw new IllegalArgumentException(
                    "specified " + SERVICE_AVAILABILITY_NAME + " is not registered in SystemRepository.");
        }
        return availability;
    }
    
    /**
     * パラメータのリクエストIDを元に、サービス提供可否状態を判定し結果を返却する。
     * 
     * @param requestId リクエストID
     * @return サービス提供可否状態を表すboolean （提供可の場合、TRUE）
     */
    public static boolean isAvailable(String requestId) {
        return getServiceAvailability().isAvailable(requestId);
    }

}
