// MOVE: commonをモジュール分割したので、nablarch.common.handlerから移動
package nablarch.common.availability;


import nablarch.core.ThreadContext;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.util.Builder;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Handler;
import nablarch.fw.InboundHandleable;
import nablarch.fw.Result;
import nablarch.fw.results.ServiceUnavailable;

/**
 * WEBサービス提供可否状態判定処理実施ハンドラ。
 * <br>
 * {@link nablarch.core.ThreadContext}から取得したリクエストIDがサービス提供可能かどうか判定する。
 * 
 * @author Masayuki Fujikuma
 * @author Masato Inoue
 * @see nablarch.common.availability.ServiceAvailability
 */
public class ServiceAvailabilityCheckHandler implements Handler<Object, Object>, InboundHandleable {

    // ------------------------------------------------------------ structure
    /**
     * サービス提供可否状態判定オブジェクト。
     */
    private ServiceAvailability serviceAvailability;
    
    /**
     * サービス提供可否判定を行う際に内部リクエストIDを使用するかどうか
     */
    private boolean usesInternalRequestId = false;


    // ----------------------------------------- implementation of Handler API
    /**
     * {@link nablarch.core.ThreadContext}からリクエストIDを取得し、サービス提供可否を判定する。<br>
     * 判定結果が可の場合、処理を後続に受け渡し、判定結果が不可の場合、例外を送出する。
     * 
     * @param inputData 入力パラメータ
     * @param context サービスハンドラチェイン
     * @return レスポンスオブジェクト
     */
    public Object handle(Object inputData, ExecutionContext context) {
        handleInbound(context);
        return context.handleNext(inputData);
    }
    
    // ----------------------------------------------------------- accessors
    /**
     * サービス提供可否状態判定オブジェクトを設定する。
     * @param serviceAvailability サービス提供可否状態判定オブジェクト
     */
    public void setServiceAvailability(
            ServiceAvailability serviceAvailability) {
        this.serviceAvailability = serviceAvailability;
    }
    
    /**
     * 開閉局状態の判定を内部リクエストIDを用いて行うか否かを設定する。
     * 
     * 明示的に設定しなかった場合のデフォルトは true (内部リクエストIDを使用する。)
     * 
     * @param usesInternal 内部リクエストIDを使用して判定を行う場合は true
     *                      常に外部から送信されたリクエストIDを使って判定を行う場合は false
     * @return このハンドラインスタンス自体
     */
    public ServiceAvailabilityCheckHandler setUsesInternalRequestId(boolean usesInternal) {
        usesInternalRequestId = usesInternal;
        return this;
    }
    
    /** ロガー */
    private static final Logger LOGGER = LoggerManager.get(ServiceAvailabilityCheckHandler.class);


    @Override
    public Result handleInbound(ExecutionContext context) {
        String requestId = usesInternalRequestId
                ? ThreadContext.getInternalRequestId()
                : ThreadContext.getRequestId();
        if (!serviceAvailability.isAvailable(requestId)) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.logTrace(Builder.concat(
                        "service unavailable. requestId=[", requestId, "]"));
            }
            throw new ServiceUnavailable();
        }
        
        return new Result.Success();
    }
}
