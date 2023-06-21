/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 21 June 2023 at 09:18
 */
package org.apache.fineract.helper;



import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class WarHelper {

    public static void sse(){

        System.err.println("--------------starting emitter son now ,will our dependencies be added as well ? -----------------");
        SseEmitter emitter = new SseEmitter();
        ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
        sseMvcExecutor.execute(() -> {
            try {
                for (int i = 0; true; i++) {
                    SseEmitter.SseEventBuilder event = SseEmitter.event()
                            .data("SSE MVC - " + LocalTime.now().toString())
                            .id(String.valueOf(i))
                            .name("sse event - mvc");
                    emitter.send(event);
                    Thread.sleep(1000);
                }
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return emitter;

    }
}
