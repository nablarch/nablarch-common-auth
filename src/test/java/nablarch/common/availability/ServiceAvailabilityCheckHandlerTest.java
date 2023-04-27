package nablarch.common.availability;

import nablarch.core.ThreadContext;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Handler;
import nablarch.fw.InboundHandleable;
import nablarch.fw.OutboundHandleable;
import nablarch.fw.invoker.BasicPipelineListBuilder;
import nablarch.fw.invoker.PipelineInvoker;
import nablarch.fw.results.ServiceUnavailable;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServiceAvailabilityCheckHandlerTest {

    private final ServiceAvailabilityCheckHandler sut = new ServiceAvailabilityCheckHandler();

    private final ServiceAvailability serviceAvailability = mock(ServiceAvailability.class);

    private final Handler<?, ?> nextHandler = mock(Handler.class);

    private final InboundOutboundHandler nextInoutHandler = mock(InboundOutboundHandler.class);
    
    private ExecutionContext createContext() {

        ExecutionContext context = new ExecutionContext();
        sut.setServiceAvailability(serviceAvailability);
        context.addHandler(sut);
        context.addHandler(nextHandler);

        return context;
    }

    private PipelineInvoker createInvoker(final ExecutionContext context) {
        PipelineInvoker invoker = new PipelineInvoker();
        BasicPipelineListBuilder builder = new BasicPipelineListBuilder();
        
        List<Object> handlerList = new ArrayList<Object>();
        ServiceAvailabilityCheckHandler target = new ServiceAvailabilityCheckHandler();
        target.setServiceAvailability(serviceAvailability);
        handlerList.add(target);
        handlerList.add(nextInoutHandler);

        builder.setHandlerList(handlerList);
        invoker.setHandlerListBuilder(builder);
        return invoker;
    }

    @Test
    public void testHandle() {

        {
            // サービスが提供されている場合
            final ExecutionContext context = createContext();
            ThreadContext.setRequestId("example");
            
            when(serviceAvailability.isAvailable("example")).thenReturn(true);
            
            context.handleNext(null);
            
            verify(nextHandler).handle(null, context);
        }
        {
            // サービスが提供されていない場合
            final ExecutionContext context = createContext();
            ThreadContext.setRequestId("example");
            when(serviceAvailability.isAvailable("example")).thenReturn(false);
    
            try {
                context.handleNext(null);
                fail("例外発生するはず");
            } catch (ServiceUnavailable e) {
                // OK
            }
            
            verify(nextInoutHandler, never()).handleInbound(context);
        }

        {
            // サービスが提供されている場合(内部リクエストIDを使う場合
            final ExecutionContext context = createContext();
            sut.setUsesInternalRequestId(true);
            ThreadContext.setInternalRequestId("internal");
    
            when(serviceAvailability.isAvailable("internal")).thenReturn(true);
            
            context.handleNext(null);
            
            verify(nextHandler).handle(null, context);
        }
        {
            // サービスが提供されていない場合

            final ExecutionContext context = createContext();
            sut.setUsesInternalRequestId(true);
            ThreadContext.setInternalRequestId("internal");
            when(serviceAvailability.isAvailable("internal")).thenReturn(false);
    
            try {
                context.handleNext(null);
                fail("例外発生するはず");
            } catch (ServiceUnavailable e) {
                // OK
            }
            
            verify(nextInoutHandler, never()).handleInbound(context);
        }
    }

    @Test
    public void testHandleInbound() {
        {
            final ExecutionContext context = createContext();
            PipelineInvoker invoker = createInvoker(context);
            ThreadContext.setRequestId("example");
            // サービスが提供されている場合
            when(serviceAvailability.isAvailable("example")).thenReturn(true);
    
            invoker.invokeInbound(context);
            
            verify(nextInoutHandler).handleInbound(context);
        }
        {
            final ExecutionContext context = createContext();
            // サービスが提供されていない場合
            when(serviceAvailability.isAvailable("example")).thenReturn(false);
    
            try {
                context.handleNext(null);
                fail("例外発生するはず");
            } catch (ServiceUnavailable e) {
                // OK
            }
            
            verify(nextInoutHandler, never()).handleInbound(context);
        }
    }

    private static interface InboundOutboundHandler extends InboundHandleable, OutboundHandleable {
        
    }
}
