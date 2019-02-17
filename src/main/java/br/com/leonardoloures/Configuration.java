package br.com.leonardoloures;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.strategicgains.repoexpress.mongodb.MongoConfig;
import com.strategicgains.restexpress.plugin.metrics.MetricsConfig;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.restexpress.RestExpress;
import org.restexpress.common.exception.ConfigurationException;
import org.restexpress.util.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Configuration extends Environment
{
	private static final String DEFAULT_EXECUTOR_THREAD_POOL_SIZE = "20";
    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

	private static final String PORT_PROPERTY = "port";
	private static final String BASE_URL_PROPERTY = "base.url";
	private static final String EXECUTOR_THREAD_POOL_SIZE = "executor.threadPool.size";

    private static MongodExecutable mongodExecutable;
	private int port;
	private String baseUrl;
	private int executorThreadPoolSize;
	private MetricsConfig metricsSettings;

	@Override
	protected void fillValues(Properties p)
	{
		this.port = Integer.parseInt(p.getProperty(PORT_PROPERTY, String.valueOf(RestExpress.DEFAULT_PORT)));
		this.baseUrl = p.getProperty(BASE_URL_PROPERTY, "http://localhost:" + String.valueOf(port));
		this.executorThreadPoolSize = Integer.parseInt(p.getProperty(EXECUTOR_THREAD_POOL_SIZE, DEFAULT_EXECUTOR_THREAD_POOL_SIZE));
		this.metricsSettings = new MetricsConfig(p);
		initializeMongo(p);
	}

	private void initializeMongo (Properties p) {
	    //Start embedded mongo in memory
        String env = p.getProperty("mongo.env");
        String uri = p.getProperty("mongodb.uri");

	    if (env != null || !env.equals("prod")){
            startEmbeddedMongoDB(uri);
        }
        MongoConfig mongo = new MongoConfig(p);

	    //TODO: map controllers, services and repos
	}

	public int getPort()
	{
		return port;
	}
	
	public String getBaseUrl()
	{
		return baseUrl;
	}
	
	public int getExecutorThreadPoolSize()
	{
		return executorThreadPoolSize;
	}

	public MetricsConfig getMetricsConfig()
    {
	    return metricsSettings;
    }

	private String getMongoIpFromUri (String uri) {
        if (uri == null) {
            throw new ConfigurationException("Please define a MongoDB URI for property: mongodb.uri");
        }
        int startIndex = uri.indexOf("//")+2;
        return uri.substring(startIndex, uri.indexOf(":", startIndex));
    }

    private Integer getMongoPortFromUri (String uri) {
        if (uri == null) {
            throw new ConfigurationException("Please define a MongoDB URI for property: mongodb.uri");
        }
        Pattern pattern = Pattern.compile(":([0-9]+)");
        Matcher matcher = pattern.matcher(uri);
        if (! matcher.find()){
            throw new ConfigurationException("MongoDB URI Bad Formatting");
        }

        return Integer.valueOf(matcher.group(1));
    }

    private void startEmbeddedMongoDB(String uri){
        MongodStarter mongodStarter = MongodStarter.getDefaultInstance();
        String bindIp = this.getMongoIpFromUri(uri);
        Integer port = this.getMongoPortFromUri(uri);

        try {
            IMongodConfig mongodConfig = new MongodConfigBuilder()
                    .version(Version.Main.PRODUCTION)
                    .net(new Net(bindIp, port, Network.localhostIsIPv6()))
                    .build();


            mongodExecutable = mongodStarter.prepare(mongodConfig);
            MongodProcess mongod = mongodExecutable.start();

            MongoClient mongo = new MongoClient(bindIp, port);
            DB db = mongo.getDB("test");
            DBCollection col = db.createCollection("testCol", new BasicDBObject());
            col.save(new BasicDBObject("testDoc", new Date()));
            db.dropDatabase();
        } catch (Exception e){
            LOG.error("Error starting Embedded MongoDB " + e);
        } finally {
            if (mongodExecutable != null){
                Runtime.getRuntime().addShutdownHook(new Thread(
                        new Runnable() {
                            public void run() {
                                LOG.info(">> Stopping MongoDB");
                                mongodExecutable.stop();
                            }
                        }
                ));
            }
        }
    }
}
