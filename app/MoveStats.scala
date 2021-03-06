package lila.openingexplorer

import java.io.{ OutputStream, InputStream }

import chess.Color

case class MoveStats(
    white: Long,
    draws: Long,
    black: Long,
    averageRatingSum: Long
) extends PackHelper {

  def total = white + draws + black

  def isEmpty = total == 0

  def averageRating: Int =
    if (total == 0) 0 else (averageRatingSum / total).toInt

  def withGameRef(game: GameRef) = {
    val avgRatingSum = averageRatingSum + game.averageRating

    game.winner match {
      case Some(Color.White) =>
        copy(white = white + 1, averageRatingSum = avgRatingSum)
      case Some(Color.Black) =>
        copy(black = black + 1, averageRatingSum = avgRatingSum)
      case None =>
        copy(draws = draws + 1, averageRatingSum = avgRatingSum)
    }
  }

  def withoutExistingGameRef(game: GameRef) = {
    val avgRatingSum = averageRatingSum - game.averageRating

    game.winner match {
      case Some(Color.White) =>
        copy(white = white - 1, averageRatingSum = avgRatingSum)
      case Some(Color.Black) =>
        copy(black = black - 1, averageRatingSum = avgRatingSum)
      case None =>
        copy(draws = draws - 1, averageRatingSum = avgRatingSum)
    }
  }

  def add(other: MoveStats) =
    new MoveStats(
      white + other.white,
      draws + other.draws,
      black + other.black,
      averageRatingSum + other.averageRatingSum
    )

  def write(out: OutputStream) = {
    writeUint(out, white)
    writeUint(out, draws)
    writeUint(out, black)
    writeUint(out, averageRatingSum)
  }
}

object MoveStats extends PackHelper {

  def empty = new MoveStats(0, 0, 0, 0)

  def fromGameRef(game: GameRef) = empty.withGameRef(game)

  def read(in: InputStream) =
    new MoveStats(readUint(in), readUint(in), readUint(in), readUint(in))
}
