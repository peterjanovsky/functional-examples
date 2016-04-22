import cats.data.{Xor, XorT}
import cats.std.future._
import scala.concurrent.{ExecutionContext, Future}

object Logic {

  def mergeBooksWithAuthorS(name: String)(implicit ec: ExecutionContext): XorT[Future, Types.Errors.Error, List[Book]] = 
    for {
      author <- Db.getAuthorByNameS(name)
      books <- XorT(Db.getBooksByAuthor(author))
    } yield books

  def mergeBooksWithAuthorN(name: String)(implicit ec: ExecutionContext): XorT[Future, Types.Errors.Error, List[Book]] = 
    for {
      author <- Db.getAuthorByNameN(name)
      books <- XorT(Db.getBooksByAuthor(author))
    } yield books

  def mergeBooksWithAuthorFF(name: String)(implicit ec: ExecutionContext): XorT[Future, Types.Errors.Error, List[Book]] = 
    for {
      author <- Db.getAuthorByNameFF(name)
      books <- XorT(Db.getBooksByAuthor(author))
    } yield books

  def mergeBooksWithAuthorSF(name: String)(implicit ec: ExecutionContext): XorT[Future, Types.Errors.Error, List[Book]] = 
    for {
      author <- Db.getAuthorByNameS(name)
      books <- XorT(Db.getBooksByAuthorF(author))
    } yield books
}

object LogicRunner {

  def execWithRecover(name: String, f: String => XorT[Future, Types.Errors.Error, List[Book]])(implicit ec: ExecutionContext): Future[Unit] =
    f(name).fold(
      { err => println(s"Error Received: $err") },
      { books => println(s"These are $name's books ${books.mkString(",")}") }
    ) recover {
        case ex => println(s"Future Error Received: $ex")
    }

  def execWithoutRecover(name: String, f: String => XorT[Future, Types.Errors.Error, List[Book]])(implicit ec: ExecutionContext): Future[Unit] =
    f(name).fold(
      { err => println(s"Error Received: $err") },
      { books => println(s"These are $name's books ${books.mkString(",")}") })
}
