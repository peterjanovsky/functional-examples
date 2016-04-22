import cats.data.{OptionT, Xor, XorT}
import cats.std.future._
import scala.concurrent.{ExecutionContext, Future}

case class Author(name: String)
case class Book(title: String, author: Author)

object Db {

  // assumes authors have unique names and your db lib returns Future[Option[Author]]
  def getAuthorByNameS(name: String)(implicit ec: ExecutionContext): XorT[Future, Types.Errors.Error, Author] = {

    val resultFO: Future[Option[Author]] = Future(Some(Author(name)))
    val optT: OptionT[Future, Author] = OptionT(resultFO)
    optT.toRight[Types.Errors.Error](Types.Errors.RecordNotFound)
  }

  def getAuthorByNameN(name: String)(implicit ec: ExecutionContext): XorT[Future, Types.Errors.Error, Author] = {

    val resultFO: Future[Option[Author]] = Future(None)
    val optT: OptionT[Future, Author] = OptionT(resultFO)
    optT.toRight[Types.Errors.Error](Types.Errors.RecordNotFound)
  }

  def getAuthorByNameFF(name: String)(implicit ec: ExecutionContext): XorT[Future, Types.Errors.Error, Author] = {

    val resultFO: Future[Option[Author]] = Future(throw new Exception("db connection pool error"))
    val optT: OptionT[Future, Author] = OptionT(resultFO)
    optT.toRight[Types.Errors.Error](Types.Errors.RecordNotFound)
  }

  def getBooksByAuthor(author: Author)(implicit ec: ExecutionContext): Future[Xor[Types.Errors.Error, List[Book]]] = {

    val resultF: Future[List[Book]] = Future(List(Book("Foo", author), Book("Bar", author)))
    resultF.map(Xor.right[Types.Errors.Error, List[Book]]) recover {
      case ex => Xor.left[Types.Errors.Error, List[Book]](Types.Errors.StoreError(ex))
    }
  }

  def getBooksByAuthorF(author: Author)(implicit ec: ExecutionContext): Future[Xor[Types.Errors.Error, List[Book]]] = {

    val resultF: Future[List[Book]] = Future(throw new Exception("db connection pool error"))
    resultF.map(Xor.right[Types.Errors.Error, List[Book]]) recover {
      case ex => Xor.left[Types.Errors.Error, List[Book]](Types.Errors.StoreError(ex))
    }
  }
}
