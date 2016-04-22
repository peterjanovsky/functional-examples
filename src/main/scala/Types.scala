object Types {

  object Errors {

    sealed trait Error
    case object RecordNotFound extends Error
    case class StoreError(throwable: Throwable) extends Error
  }
}
