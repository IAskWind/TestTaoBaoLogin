package demo;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;

public class DemoConfig extends JFinalConfig {

	public void configConstant(Constants me) {
		me.setDevMode(true);
		me.setViewType(ViewType.FREE_MARKER);
		
	}

	public void configRoute(Routes me) {
		me.setBaseViewPath("WEB-INF/Views");
		me.add("/hello", HelloController.class);
		me.add("/", IndexController.class);
		
	}

	public void configEngine(Engine me) {
		
	}

	public void configPlugin(Plugins me) {
	}

	public void configInterceptor(Interceptors me) {
	}

	public void configHandler(Handlers me) {
		me.add(new ContextPathHandler("root"));
	}
	
	public static void main(String[] args) {
		JFinal.start("WebRoot", 8080, "/", 5);
	}
}
