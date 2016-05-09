package models.flower

import base.SpecBase
import org.specs2.mutable.Specification
import play.api.Logger
import play.test.WithApplication

class Flower$Test extends SpecBase {

    "Flower$Test" should {
        "searchImg" in {
            Logger.debug(Flower.searchImg("http://img1.daumcdn.net/thumb/R120x0/?fname=http://t1.beta.daumcdn.net/daisy/flowers/0.jpg"))
            ok
        }
    }
}
