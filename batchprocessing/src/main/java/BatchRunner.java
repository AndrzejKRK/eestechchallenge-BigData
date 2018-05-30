import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Session;
import com.datastax.spark.connector.cql.CassandraConnector;
import com.datastax.spark.connector.japi.CassandraJavaUtil;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SparkSession;

import java.util.Date;

public class BatchRunner {

    public static final String CASSANDRA_KEYSPACE = "keyspace_name";

    public static void main(String[] args) {

        SparkConf sparkConf = new SparkConf(true);
        sparkConf.setAppName("twitter-hackaton-mashup");
        sparkConf.setMaster("local[*]");
        sparkConf.set("spark.cassandra.connection.host", "localhost");
        sparkConf.set("spark.cassandra.connection.port", "9042");


        SparkContext sparkContext = new SparkContext(sparkConf);

        exampleJob(sparkConf, sparkContext);

    }

    public static void exampleJob(SparkConf sparkConf, SparkContext sparkContext) {
//        long counter = CassandraJavaUtil.javaFunctions(sparkContext)
//                .cassandraTable(CASSANDRA_KEYSPACE, "sample_table")
//                .count();
//
//        System.out.println("Size of processed resultset: " + counter);

        SparkSession sparkSession = SparkSession
                .builder()
                .config(sparkConf)
                .getOrCreate();

        CassandraConnector connector = CassandraConnector.apply(sparkSession.sparkContext().conf());
        Session session = connector.openSession();
        BoundStatement bs = session.prepare("insert into keyspace_name.sample_table" +
                "(id, keyword, tweet_date, language, country, followers_len) " +
                "values (?,?,?,?,?,?)")
                .bind(3L, "test", new Date(), "test","test",3);
        session.execute(bs);
        session.close();
    }
}
