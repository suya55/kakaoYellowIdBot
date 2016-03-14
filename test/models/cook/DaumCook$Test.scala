package models.cook

import org.specs2.mutable.Specification
import play.api.Logger
import play.api.libs.json.Json

/**
  * Created by suya55 on 3/14/16.
  */
class DaumCook$Test extends Specification {

    "DaumCook$Test" should {
        "recommendRecipe" in {
            (0 to 30).map{idx =>
                val result = DaumCook.recommendRecipe()
                Logger.info(Json.toJson(result).toString())
                result must_!= null
                result.photo.shouldNotEqual(null)
            }
        }

    }
}
