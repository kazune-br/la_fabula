package controllers

import java.nio.file.Paths

import javax.inject.Inject
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.{ LogoutEvent, Silhouette }
import models.daos.UserDAO
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.mvc._
import utils.auth.DefaultEnv
import play.api.libs.Files
import play.api.libs.Files.TemporaryFile
import play.api.http.HttpEntity
import akka.util.ByteString

// implicit ec: ExecutionContext
import scala.concurrent.{ ExecutionContext, Future }

class UserAccountController @Inject() (
  userDAO: UserDAO,
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv]
)(
  implicit
  webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ec: ExecutionContext
) extends AbstractController(components) with I18nSupport {

  def edit = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Future.successful(Ok(views.html.edit(request.identity)))
  }

  def upload = silhouette.SecuredAction.async(parse.multipartFormData) { implicit request: SecuredRequest[DefaultEnv, MultipartFormData[Files.TemporaryFile]] =>

    val tmp = request.body.file("image").map { image =>
      val filename = Paths.get(image.filename).getFileName
      image.ref.moveTo(Paths.get(s"./public/images/$filename"), replace = true)
      // userDAO.insertImage(request.identity, filename)
      userDAO.insertImage(request.identity, filename.toString)

      Redirect(routes.UserAccountController.edit)
      // Result(303, HttpEntity.NoEntity)
      //      Result(
      //        header = ResponseHeader(200, Map.empty),
      //        body = HttpEntity.Strict(ByteString("Hello world!"), Some("text/plain"))
      //      )
      //    }.getOrElse {
      //      // Redirect(routes.UserAccountController.edit)
      //      Result(
      //        header = ResponseHeader(200, Map.empty),
      //        body = HttpEntity.Strict(ByteString("Hello world!"), Some("text/plain"))
      //      )
      //    }
    }.get
    Future(tmp)
  }
}
