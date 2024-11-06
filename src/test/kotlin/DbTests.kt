import com.rpgportugal.orthanc.kt.persistence.repository.job.db.SqlJobRepository
import org.junit.jupiter.api.Test
import org.ktorm.database.Database


class DbTests {

    @Test
    fun dbBasic(){
        val database = Database.connect(url="")
        val repo = SqlJobRepository(database)
        repo.getSql("job1")
    }
}