package verticle;
import org.vertx.groovy.core.http.RouteMatcher
import support.Renderer

def log = container.logger
def routeMatcher = new RouteMatcher()
def server = vertx.createHttpServer()

def renderer = new Renderer()

// leave this config in case web-server mod supports passing in a routeMatcher
def webServerConfig = [
	web_root : "web",
	index_page : "index.html",
	host :"localhost",
	port : 8080,
	//static_files : true,
	//gzip_files : true,

	//ssl : <ssl>,
	//key_store_password : <key_store_password>,
	//key_store_path : <key_store_path>,

	//bridge : <bridge>,
	inbound_permitted : [[:]],
	outbound_permitted : [[:]],
	sjs_config : [prefix: "/eventbus"],
	//auth_timeout : <auth_timeout>,
	//auth_address : <auth_address>
]

routeMatcher.with {
	get("/test") { req ->
		testPage = renderer.renderTestPage()
		log.info(testPage)
		req.response.end(testPage, "UTF-8")
	}
	getWithRegEx(".*") { req ->
		log.info(req.uri)
		def file = (req.uri == "/" ? "/index.html" : req.uri)
		req.response.sendFile("web${file}")
	}
}

server.requestHandler(routeMatcher.asClosure())
vertx.createSockJSServer(server).bridge(webServerConfig.sjs_config, webServerConfig.inbound_permitted, webServerConfig.outbound_permitted)
server.listen(8080, "localhost")
