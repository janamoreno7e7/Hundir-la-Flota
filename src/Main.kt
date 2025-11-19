// --- IMPORTACIONS ---
// L'"import" és com dir-li a Kotlin: "Necessitaré eines que estan a la capsa 'kotlin.random'".
// Concretament, necessitem 'Random' per poder generar números aleatoris.
import kotlin.random.Random

// --- DEFINICIONS GLOBALS ---
// Aquestes són definicions que tot el nostre programa podrà veure i fer servir.

// 1. Definim els estats possibles d'una cel·la
// Un 'enum class' és un tipus de dada especial que restringeix una variable
// a un conjunt fix de valors (com els estats d'un semàfor).
// És molt més segur i llegible que fer servir números (ex. 0 per AIGUA, 1 per VAIXELL).
enum class EstadoCelda {
    AIGUA,    // La cel·la és aigua i no s'ha disparat.
    VAIXELL,  // La cel·la conté una part d'un vaixell (ocult a l'oponent).
    TOCAT,    // La cel·la tenia un vaixell i ha estat colpejada.
    FALLAT    // La cel·la era aigua i ha estat disparada.
}

// 2. Definim constants globals
// 'const val' defineix una constant de temps de compilació.
// "const" significa que el seu valor (10) es coneix abans d'executar el programa,
// la qual cosa és molt eficient.
// "val" significa que és un valor (value) que no pot canviar.
const val TAMANO_TABLERO = 10

// 3. Creem la CLASSE Tauler
// Una 'class' (classe) és un "motlle" o "plànol" per crear objectes.
// En lloc de tenir un sol 'tablero' global, ara podem crear
// 'taulerJ1 = Tauler()' i 'taulerJ2 = Tauler()'.
// Cada "instància" (objecte) d'aquesta classe tindrà les seves pròpies
// variables 'flota' i 'tablero' internes.
class Tauler {

    // 3.1. Definim la flota DINS de la classe
    // Aquesta llista defineix les mides dels vaixells que es col·locaran.
    // 'private' vol dir que només es pot veure i fer servir des de DINS
    // d'aquesta classe. Ningú de fora pot accedir a 'taulerJ1.flota'.
    // 'val' vol dir que la llista no es pot reassignar.
    // 'listOf' crea una llista immutable (no s'hi poden afegir o treure vaixells).
    private val flota = listOf(5, 4, 3, 3, 2) // Total 5 vaixells

    // 3.2. Definim el tauler DINS de la classe
    // Aquest és el "tauler" (la graella 2D) d'aquest objecte.
    // És 'private' per protegir-lo (encapsulació).
    // El 'Array(TAMANO_TABLERO)' crea l'array "exterior" (les 10 files).
    // El bloc '{ ... }' (un lambda) s'executa 10 cops, un per cada fila.
    // 'Array(TAMANO_TABLERO) { EstadoCelda.AIGUA }' crea l'array "interior"
    // (les 10 columnes per a CADA fila) i omple cada cel·la
    // amb l'estat inicial 'EstadoCelda.AIGUA'.
    private val tablero = Array(TAMANO_TABLERO) {
        Array(TAMANO_TABLERO) { EstadoCelda.AIGUA }
    }

    /**
     * MÈTODE: Col·loca la flota aleatòriament DINS d'aquest tauler.
     * Un "mètode" és una funció que pertany a una classe.
     */
    fun posicionarFlota() {
        // 'for (tamanoBarco in flota)' és un bucle "for-each".
        // Iterarà sobre cada element de la llista 'flota'.
        // 1r cop: tamanoBarco = 5
        // 2n cop: tamanoBarco = 4
        // 3r cop: tamanoBarco = 3
        // ...etc.
        for (tamanoBarco in flota) {

            // 'var' defineix una variable que SÍ pot canviar (mutable).
            // Aquesta és una variable "bandera" (flag).
            // La fem servir per controlar el bucle 'while' de sota.
            var posicionado = false

            // 'while (!posicionado)' vol dir "mentre 'posicionado' sigui 'false'".
            // Aquest és un bucle de "reintent". Continuarà generant
            // posicions aleatòries fins que en trobi una de vàlida.
            while (!posicionado) {
                // Genera un valor aleatori 'true' (Horitzontal) o 'false' (Vertical).
                val esHorizontal = Random.nextBoolean()

                // Genera un número aleatori entre 0 (inclòs) i 10 (exclòs),
                // és a dir, 0..9, que són els índexs de les nostres files.
                val fila = Random.nextInt(TAMANO_TABLERO)

                // Genera un número aleatori 0..9 per a les columnes.
                val col = Random.nextInt(TAMANO_TABLERO)

                // Crida a un altre mètode (dins d'aquesta mateixa classe)
                // per comprovar si el vaixell hi cap i no xoca.
                if (esPosicionValida(tamanoBarco, fila, col, esHorizontal)) {

                    // Si 'esPosicionValida' retorna 'true', llavors
                    // cridem al mètode per "pintar" el vaixell al tauler.
                    colocarBarco(tamanoBarco, fila, col, esHorizontal)

                    // Hem aconseguit col·locar el vaixell.
                    // Canviem la bandera a 'true' per sortir del bucle 'while'
                    // i que el 'for' pugui passar al següent vaixell de la flota.
                    posicionado = true
                }
                // Si 'esPosicionValida' retorna 'false', no fem res.
                // El 'while' es repetirà, generant nous números aleatoris.
            }
        }
    }

    /**
     * MÈTODE PRIVAT: Comprova si un vaixell es pot col·locar.
     * És 'private' perquè només l'ha de fer servir 'posicionarFlota'.
     * Ningú des de fora de la classe necessita cridar-lo.
     * Rep la mida, la posició inicial (fila, col) i l'orientació.
     * Retorna un 'Boolean': 'true' (és vàlid) o 'false' (no és vàlid).
     */
    private fun esPosicionValida(tamano: Int, fila: Int, col: Int, esHorizontal: Boolean): Boolean {
        // 1. Comprovar límits del tauler
        if (esHorizontal) {
            // Si és horitzontal, comprovem que no surti per la dreta.
            // Ex: col(8) + tamano(3) = 11. 11 és > 10, per tant, 'return false'.
            // 'return false' atura l'execució d'aquest mètode immediatament.
            if (col + tamano > TAMANO_TABLERO) return false
        } else {
            // Si és vertical, comprovem que no surti per baix.
            // Ex: fila(9) + tamano(2) = 11. 11 és > 10, per tant, 'return false'.
            if (fila + tamano > TAMANO_TABLERO) return false
        }

        // 2. Comprovar superposició (xocs amb altres vaixells)
        // 'for (i in 0 until tamano)' és un bucle que compta: 0, 1, 2...
        // fins a (tamano - 1).
        // Ex: si tamano=3, 'i' serà 0, 1, 2.
        for (i in 0 until tamano) {
            if (esHorizontal) {
                // Comprova la cel·la [fila][col+0], [fila][col+1], [fila][col+2]...
                // Si *alguna* d'aquestes cel·les ja és 'VAIXELL', 'return false'.
                if (tablero[fila][col + i] == EstadoCelda.VAIXELL) return false
            } else {
                // Comprova la cel·la [fila+0][col], [fila+1][col], [fila+2][col]...
                if (tablero[fila + i][col] == EstadoCelda.VAIXELL) return false
            }
        }

        // Si el codi arriba fins aquí, vol dir que ha passat les dues
        // comprovacions (límits i superposició). Per tant, la posició ÉS vàlida.
        return true
    }

    /**
     * MÈTODE PRIVAT: "Pinta" el vaixell al tauler.
     * Només es crida DESPRÉS que 'esPosicionValida' hagi retornat 'true'.
     * Per això, no cal que tornem a comprovar els límits aquí.
     */
    private fun colocarBarco(tamano: Int, fila: Int, col: Int, esHorizontal: Boolean) {
        // Bucle per a cada segment del vaixell (0, 1, 2...)
        for (i in 0 until tamano) {
            if (esHorizontal) {
                // MODIFICA l'estat del 'tablero' d'aquesta classe
                // a la coordenada horitzontal corresponent.
                tablero[fila][col + i] = EstadoCelda.VAIXELL
            } else {
                // MODIFICA l'estat del 'tablero' d'aquesta classe
                // a la coordenada vertical corresponent.
                tablero[fila + i][col] = EstadoCelda.VAIXELL
            }
        }
    }

    /**
     * MÈTODE PÚBLIC: Imprimeix la vista del propietari del tauler.
     * És 'public' (per defecte) perquè es cridarà des de 'fun main'.
     * Aquesta vista mostra els vaixells propis (la 'V').
     */
    fun imprimirTableroPropi() {
        // Imprimeix la capçalera de les lletres (columnes)
        println("  A B C D E F G H I J")

        // Bucle per a cada fila (0..9)
        for (fila in 0 until TAMANO_TABLERO) {

            // Imprimeix el número de fila (1..10)
            // 'fila + 1' perquè els humans compten des de 1.
            // 'String.format("%-2d", ...)' és per formatar el text:
            // "%-2d" vol dir "un número (-d) alineat a l'esquerra (-)
            // que ocupi 2 espais (2)". Això fa que el "10" no
            // desquadri la graella.
            // 'print' (sense 'ln') NO fa un salt de línia al final.
            print(String.format("%-2d", fila + 1))

            // Bucle interior: per a cada columna (0..9) DINS de la fila actual
            for (col in 0 until TAMANO_TABLERO) {

                // 'when' és una eina molt potent de Kotlin (com un 'switch').
                // Comprova el valor de 'tablero[fila][col]'...
                val celda = when (tablero[fila][col]) {
                    // ...si és AIGUA, 'celda' valdrà "·"
                    EstadoCelda.AIGUA -> "·"
                    // ...si és VAIXELL, 'celda' valdrà "V"
                    EstadoCelda.VAIXELL -> "V" // !! CLAU: Els vaixells propis ES VEUEN
                    // ...etc.
                    EstadoCelda.TOCAT -> "X"
                    EstadoCelda.FALLAT -> "F"
                }
                // Imprimeix el caràcter de la cel·la i un espai,
                // i es manté a la mateixa línia.
                print("$celda ")
            }
            // Quan el bucle de columnes acaba, fem un 'println()' buit
            // per fer un salt de línia i passar a la següent fila.
            println()
        }
    }

    /**
     * MÈTODE PÚBLIC: Imprimeix la vista de l'oponent (la "boira de guerra").
     * Aquesta vista amaga els vaixells enemics.
     */
    fun imprimirTableroOponent() {
        // La impressió és gairebé idèntica a 'imprimirTableroPropi'...
        println("  A B C D E F G H I J")
        for (fila in 0 until TAMANO_TABLERO) {
            print(String.format("%-2d", fila + 1))
            for (col in 0 until TAMANO_TABLERO) {
                val celda = when (tablero[fila][col]) {
                    // ...excepte per aquesta línia:
                    EstadoCelda.AIGUA -> "·"
                    EstadoCelda.VAIXELL -> "·" // !! CLAU: Els vaixells enemics S'AMAGUEN com a aigua
                    EstadoCelda.TOCAT -> "X"
                    EstadoCelda.FALLAT -> "F"
                }
                print("$celda ")
            }
            println()
        }
    }

    /**
     * MÈTODE PÚBLIC: Processa un tret en aquest tauler.
     * Aquest mètode el crida el jugador atacant SOBRE el tauler de l'oponent.
     * @param fila La fila (0-9) on es dispara.
     * @param col La columna (0-9) on es dispara.
     * @return Retorna l'estat resultant (TOCAT, FALLAT) per informar a l'atacant.
     */
    fun disparar(fila: Int, col: Int): EstadoCelda {
        // 1. Llegim l'estat ACTUAL de la cel·la abans de fer res.
        val estadoActual = tablero[fila][col]

        // 2. Fem servir 'when' per decidir què fer I retornar el resultat.
        return when (estadoActual) {

            // Cas 1: Hi ha un vaixell!
            EstadoCelda.VAIXELL -> {
                // MODIFIQUEM el nostre tauler: la cel·la passa a 'TOCAT'.
                tablero[fila][col] = EstadoCelda.TOCAT
                // RETORNEM 'TOCAT' per informar que ha estat un èxit.
                EstadoCelda.TOCAT
            }

            // Cas 2: Hi ha aigua!
            EstadoCelda.AIGUA -> {
                // MODIFIQUEM el nostre tauler: la cel·la passa a 'FALLAT'.
                tablero[fila][col] = EstadoCelda.FALLAT
                // RETORNEM 'FALLAT' per informar que ha fallat.
                EstadoCelda.FALLAT
            }

            // Cas 3: El tret és repetit (la cel·la ja era 'TOCAT' o 'FALLAT').
            // 'else' captura qualsevol altre cas.
            else -> estadoActual // No fem res i retornem l'estat que ja hi havia.
        }
    }

    /**
     * MÈTODE PÚBLIC: Consulta l'estat d'una cel·la sense modificar-la.
     * El fem servir per comprovar si un jugador està repetint un tret
     * ABANS de cridar a 'disparar()'.
     * @return L'estat actual de la cel·la (AIGUA, VAIXELL, TOCAT, FALLAT).
     */
    fun consultarEstat(fila: Int, col: Int): EstadoCelda {
        // Simplement llegeix el valor del tauler i el retorna.
        return tablero[fila][col]
    }

    /**
     * MÈTODE PÚBLIC: Comprova si el propietari d'aquest tauler ha perdut.
     * @return 'true' si no queden cel·les 'VAIXELL', 'false' si en queda almenys una.
     */
    fun haPerdut(): Boolean {
        // 'for (fila in tablero)' itera sobre cadascun dels 10 arrays (files)
        for (fila in tablero) {
            // 'for (celda in fila)' itera sobre cadascuna de les 10 cel·les
            // (EstadoCelda) DINS de l'array 'fila' actual.
            for (celda in fila) {
                // Comprovem l'estat de la cel·la
                if (celda == EstadoCelda.VAIXELL) {
                    // Si trobem UNA SOLA cel·la que encara sigui 'VAIXELL',
                    // significa que al jugador encara li queden vaixells.
                    // Per tant, NO ha perdut. Aturem la funció i retornem 'false'.
                    return false
                }
            }
        }
        // Si els dos bucles 'for' acaben sense haver trobat
        // cap 'EstadoCelda.VAIXELL', significa que tots els vaixells
        // estan enfonsats (són 'TOCAT').
        // Per tant, el jugador SÍ que ha perdut. Retornem 'true'.
        return true
    }
} // --- FI DE LA CLASSE Tauler ---


/**
 * FUNCIÓ AUXILIAR (fora de la classe): Converteix l'entrada de l'usuari (ex. "B5")
 * a coordenades de l'array (ex. fila=4, col=1).
 * És 'private' perquè només la farem servir des d'aquest fitxer (des de 'main').
 *
 * @param input El text (String) introduït per l'usuari.
 * @return Un 'Pair<Int, Int>?'
 * 'Pair' és un objecte simple que conté dos valors (la fila i la columna).
 * El '?' significa que aquesta funció pot retornar un 'Pair' o pot
 * retornar 'null' si l'entrada de l'usuari és invàlida.
 */
private fun convertirCoordenades(input: String): Pair<Int, Int>? {
    // 1. Validació de la longitud (ex. "A5" té longitud 2, "A10" té longitud 3)
    if (input.length < 2 || input.length > 3) {
        return null // Format incorrecte
    }

    // 2. Convertim a majúscules per acceptar "a5" i "A5" per igual.
    val inputValidat = input.uppercase()

    // 3. Extreure la columna (Lletra)
    // 'inputValidat[0]' agafa el primer caràcter (ex. 'A').
    val colChar = inputValidat[0]
    // Comprovem que el caràcter estigui en el rang de 'A' a 'J'.
    if (colChar !in 'A'..'J') {
        return null // Lletra invàlida
    }
    // Arimètica de caràcters: 'A' - 'A' = 0, 'B' - 'A' = 1, 'C' - 'A' = 2...
    val col = colChar - 'A'

    // 4. Extreure la fila (Número)
    // 'inputValidat.substring(1)' agafa la resta de l'string (ex. "5" o "10").
    // '.toIntOrNull()' intenta convertir aquest text a un número (Int).
    // Si no pot (ex. "A!"), retorna 'null' en lloc de petar.
    val filaNum = inputValidat.substring(1).toIntOrNull()

    // Comprovem que 'filaNum' no sigui nul (és a dir, era un número)
    // i que estigui en el rang humà (1 a 10).
    if (filaNum == null || filaNum !in 1..10) {
        return null // Número invàlid
    }
    // Convertim el número humà (1-10) a l'índex de l'array (0-9).
    val fila = filaNum - 1

    // 5. Si tot és correcte, retornem un 'Pair' amb la fila i la columna.
    return Pair(fila, col)
}


// --- FUNCIÓ PRINCIPAL: El director d'orquestra del joc ---
// 'fun main()' és el punt d'entrada de qualsevol programa Kotlin.
// El codi comença a executar-se aquí.
fun main() {
    println("--- BENVINGUTS A HUNDIR LA FLOTA (2 JUGADORS) ---")

    // 1. Creem els dos objectes 'Tauler'
    // Cridem al "constructor" de la classe Tauler (el 'motlle')
    // per crear una instància (un objecte real) per al Jugador 1.
    println("Creant tauler del Jugador 1...")
    val taulerJ1 = Tauler()

    // Creem una SEGONA instància, totalment independent, per al Jugador 2.
    println("Creant tauler del Jugador 2...")
    val taulerJ2 = Tauler()

    // 2. Col·loquem les flotes a cada tauler de forma independent
    // Cridem al mètode 'posicionarFlota()' de l'objecte 'taulerJ1'.
    println("Col·locant flota del Jugador 1...")
    taulerJ1.posicionarFlota()
    // Cridem al mètode 'posicionarFlota()' de l'objecte 'taulerJ2'.
    println("Col·locant flota del Jugador 2...")
    taulerJ2.posicionarFlota()

    println("Flotes desplegades! Comença la batalla.")
    println("-------------------------------------------")

    // 3. Variables per controlar el joc
    // 'var' perquè aquests valors canviaran durant el joc.
    var tornJugador1 = true // Bandera (flag) que controla de qui és el torn.
    var jocAcabat = false   // Bandera (flag) que controla el bucle principal del joc.

    // 4. Bucle de joc principal
    // 'while (!jocAcabat)' vol dir "mentre 'jocAcabat' sigui 'false'".
    // Aquest bucle s'executarà un cop per torn, fins que algú guanyi.
    while (!jocAcabat) {

        // 4.1. Determinar qui juga i qui rep en AQUEST torn.
        // Aquesta és una forma avançada d'assignar 3 variables alhora
        // (es diu "destructuring" d'una 'Triple').
        val (nomJugador, taulerPropi, taulerOponent) = if (tornJugador1) {
            // Si 'tornJugador1' és 'true', J1 ataca J2.
            Triple("Jugador 1", taulerJ1, taulerJ2)
        } else {
            // Si 'tornJugador1' és 'false', J2 ataca J1.
            Triple("Jugador 2", taulerJ2, taulerJ1)
        }
        // Ara, 'nomJugador' té el nom correcte, 'taulerPropi' té el tauler
        // del jugador actiu, i 'taulerOponent' té el tauler de qui rep.

        println("\n>>> TORN DEL $nomJugador <<<")

        // 4.2. Mostrar els taulers corresponents al torn
        println("\nEL TEU TAULER (els teus vaixells):")
        // Cridem el mètode 'imprimirTableroPropi()' sobre el tauler
        // del jugador que té el torn.
        taulerPropi.imprimirTableroPropi()

        println("\nTAULER DE L'OPONENT (on dispararàs):")
        // Cridem el mètode 'imprimirTableroOponent()' sobre el tauler
        // del jugador que NO té el torn.
        taulerOponent.imprimirTableroOponent()

        // 4.3. Bucle per demanar coordenades VÀLIDES
        // Declarem la variable 'coordenadesTret' fora del bucle,
        // inicialitzada a 'null'.
        var coordenadesTret: Pair<Int, Int>? = null

        // 'while (coordenadesTret == null)'
        // Aquest bucle es repetirà fins que 'coordenadesTret'
        // contingui un 'Pair' vàlid (és a dir, deixi de ser 'null').
        while (coordenadesTret == null) {

            // 4.3.1. Demanem l'entrada
            // 'print' (sense 'ln') per deixar el cursor a la mateixa línia.
            print("\n$nomJugador, introdueix coordenades per disparar (ex. A5): ")

            // 'readlnOrNull()' llegeix el que l'usuari escriu (pot ser 'null').
            // '?.trim()' treu espais en blanc del principi i final.
            // '?.uppercase()' ho converteix a majúscules.
            // '?: ""' (operador Elvis) diu: "si el resultat és 'null',
            // fes servir una cadena buida '""' en lloc seu".
            val inputUsuari = readlnOrNull()?.trim()?.uppercase() ?: ""

            // 4.3.2. Intentem convertir l'entrada cridant la nostra funció auxiliar.
            // Si 'inputUsuari' és "A5", 'coordenadesTret' serà Pair(4, 0).
            // Si 'inputUsuari' és "X99", 'coordenadesTret' serà 'null'.
            coordenadesTret = convertirCoordenades(inputUsuari)

            // 4.3.3. Validació 1: Format correcte
            if (coordenadesTret == null) {
                // Si 'coordenadesTret' és 'null', el format era invàlid.
                println("Coordenades invàlides. Si us plau, introdueix un format com 'B7' o 'J10'.")
                // El bucle 'while' es repetirà.

            } else {
                // 4.3.4. Validació 2: Tret repetit
                // Si el format és bo, hem de comprovar que no hagi disparat ja.
                // Desfem el 'Pair' en dues variables (fila i col).
                val (fila, col) = coordenadesTret

                // Cridem al mètode 'consultarEstat' (el de només llegir)
                // sobre el tauler de l'OPONENT.
                val estatPrevist = taulerOponent.consultarEstat(fila, col)

                // Comprovem si la cel·la ja estava disparada.
                if (estatPrevist == EstadoCelda.TOCAT || estatPrevist == EstadoCelda.FALLAT) {
                    println("Ja havies disparat a $inputUsuari! Tria unes altres coordenades.")
                    // **TRUC**: Tornem a posar 'coordenadesTret' a 'null'
                    // per forçar que el bucle 'while' es repeteixi
                    // i torni a demanar coordenades.
                    coordenadesTret = null
                }
                // Si no era TOCAT ni FALLAT, 'coordenadesTret' manté el seu
                // valor 'Pair', i el bucle 'while' s'aturarà.
            }
        } // Fi del bucle 'while (coordenadesTret == null)'

        // 4.4. Tenim coordenades vàlides i no repetides.
        // Kotlin és prou llest per saber que 'coordenadesTret' NO pot ser 'null' aquí.
        // Tornem a desfer el 'Pair' (ara en variables finals per al tret).
        val (filaTret, colTret) = coordenadesTret

        println("$nomJugador dispara...")

        // 4.5. Processar el tret al tauler de l'OPONENT
        // Ara cridem 'disparar()' (el mètode que MODIFICA el tauler)
        // sobre 'taulerOponent'.
        // Guardem el resultat (TOCAT o FALLAT) a 'resultatTret'.
        val resultatTret = taulerOponent.disparar(filaTret, colTret)

        // 4.6. Informar del resultat
        // Fem servir 'when' sobre el resultat que ens ha retornat 'disparar()'.
        when (resultatTret) {
            EstadoCelda.TOCAT -> println(">>> RESULTAT: TOCAT! :)")
            EstadoCelda.FALLAT -> println(">>> RESULTAT: AIGUA! :(")
            // No cal 'else' perquè la nostra validació 4.3.4
            // ja ha filtrat els trets repetits.
            else -> {} // No fem res
        }

        // 4.7. Comprovar si l'oponent ha perdut
        // Després de disparar, cridem 'haPerdut()' sobre 'taulerOponent'.
        if (taulerOponent.haPerdut()) {
            // Si 'haPerdut()' retorna 'true':
            println("\n===========================================")
            println(" FELICITATS $nomJugador! HAS GUANYAT!")
            println("===========================================")

            // Canviem la bandera principal del joc a 'true'.
            // Això farà que el bucle 'while (!jocAcabat)' s'aturi.
            jocAcabat = true

        } else {
            // 4.8. Si ningú ha guanyat, canviem de torn

            // '!' és l'operador de negació (NOT).
            // Si 'tornJugador1' era 'true', ara serà 'false'.
            // Si 'tornJugador1' era 'false', ara serà 'true'.
            tornJugador1 = !tornJugador1

            // 4.9. Pausa i "neteja" de pantalla per al canvi de jugador
            // Això és crucial per a un joc "hot-seat" (mateixa cadira).
            println("\nPrem ENTER per netejar la pantalla i passar el torn al següent jugador...")

            // 'readlnOrNull()' atura l'execució del programa
            // fins que l'usuari prem la tecla ENTER.
            readlnOrNull()

            // 'repeat(50) { ... }' executa el bloc 50 cops.
            // Imprimim 50 línies en blanc per "netejar" la consola
            // i amagar el tauler del jugador actual al següent.
            repeat(50) { println() }
        }
    } // El 'while' torna a començar (o s'atura si 'jocAcabat' és 'true')
} // --- FI DE LA FUNCIÓ main ---