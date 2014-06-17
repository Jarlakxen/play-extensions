package utils

import scala.reflect.macros.Context
import scala.language.experimental.macros

object Templates {

  def byName(rootPackage: String) = macro Macros.byName

  object Macros {

    def byName(c: Context)(rootPackage: c.Expr[String]) = {
      import c.universe._

      val pkg = rootPackage.tree match {
        case Literal(Constant(name: String)) => c.mirror.staticPackage(name)
      }

      val templates = for {
        member <- pkg.typeSignature.members
        if member.typeSignature <:< typeOf[play.twirl.api.BaseScalaTemplate[_, _]]
      } yield q"${member.name.decoded} -> $member"

      // TODO return a value which has page names as members
      c.Expr[Any](q"Map(..$templates)")
    }

  }

}