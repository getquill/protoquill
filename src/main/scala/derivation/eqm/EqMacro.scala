// package derivation.eqm

// import scala.deriving._
// import scala.quoted._

// import scala.compiletime.{erasedValue, summonFrom, constValue}



// trait Eq[T] {
//   def eqv(x: T, y: T): Boolean
// }

// object Eq {
//   given Eq[String] {
//     def eqv(x: String, y: String) = x == y
//   }

//   given Eq[Int] {
//     def eqv(x: Int, y: Int) = x == y
//   }

//   def eqProduct[T](body: (T, T) => Boolean): Eq[T] =
//     new Eq[T] {
//       def eqv(x: T, y: T): Boolean = body(x, y)
//     }

//   def eqSum[T](body: (T, T) => Boolean): Eq[T] =
//     new Eq[T] {
//       def eqv(x: T, y: T): Boolean = body(x, y)
//     }

//   def summonAll[T: Type](using Quotes): List[Expr[Eq[_]]] = 
//     // TODO Make a PR proposing this change
//     Type.of[T] match {
//       case '[tpe *: tpes] => 
//         Expr.summon[Eq[tpe]] match {
//           case Some(value) => value :: summonAll[tpes]
//         }
//       case '[EmptyTuple] => Nil
//     }



//   implicit def derived[T: Type](using Quotes): Expr[Eq[T]] = {
//     import quotes.reflect._

//     val ev: Expr[Mirror.Of[T]] = Expr.summon[Mirror.Of[T]].get

//     ev match {
//       case '{ $m: Mirror.ProductOf[T] { type MirroredElemTypes = elementTypes }} =>
//         val elemInstances = summonAll[elementTypes]
//         val eqProductBody: (Expr[T], Expr[T]) => Expr[Boolean] = (x, y) => {
//           elemInstances.zipWithIndex.foldLeft(Expr(true: Boolean)) {
//             case (acc, (elem, index)) =>
//               import miniquill.quoter.Dsl.autoQuote
//               val e1 = '{$x.asInstanceOf[Product].productElement(${Expr(index)})}
//               val e2 = '{$y.asInstanceOf[Product].productElement(${Expr(index)})}

//               '{ $acc && $elem.asInstanceOf[Eq[Any]].eqv($e1, $e2) }
//           }
//         }
//         '{
//           eqProduct((x: T, y: T) => ${eqProductBody('x, 'y)})
//         }

//       case '{ $m: Mirror.SumOf[T] { type MirroredElemTypes = elementTypes }} =>
//         val elemInstances = summonAll[elementTypes]
//         val eqSumBody: (Expr[T], Expr[T]) => Expr[Boolean] = (x, y) => {
//           val ordx = '{ $m.ordinal($x) }
//           val ordy = '{ $m.ordinal($y) }

//           val elements = Expr.ofList(elemInstances)
//           '{
//               $ordx == $ordy && $elements($ordx).asInstanceOf[Eq[Any]].eqv($x, $y)
//           }
//         }

//         '{
//           eqSum((x: T, y: T) => ${eqSumBody('x, 'y)})
//         }
//     }
//   }
// }

// object EqMacro {
//   extension [T](x: =>T):
//     inline def ===(y: =>T)(using eq: Eq[T]): Boolean = eq.eqv(x, y)

//   implicit inline def eqGen[T]: Eq[T] = ${ Eq.derived[T] }
// }
