import com.rpgportugal.orthanc.kt.persistence.sql.job.SqlJobRepository
import org.junit.jupiter.api.Test
import org.ktorm.database.Database


class DbTests {

    @Test
    fun dbBasic() {
        val database = Database.connect(url = "")
        val repo = SqlJobRepository(database)
        repo.getSql("job1")
    }
}