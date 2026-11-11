package com.example.booksapp.ui.pantallas.lector

import com.example.booksapp.datos.modelos.LibroModelo

/**
 * Modelo de datos para un capítulo dentro del lector.
 */
data class CapituloModelo(
    val numero: Int,
    val titulo: String,
    val parrafos: List<String>
)

/**
 * Proveedor de contenido literario para la experiencia de lectura en BooksApp.
 */
object ContenidoLibrosLector {

    /**
     * Retorna la lista de capítulos disponibles para un libro determinado.
     */
    fun obtenerCapitulos(libro: LibroModelo): List<CapituloModelo> {
        val tituloMinusculas = libro.titulo.lowercase()
        return when {
            tituloMinusculas.contains("cien años de soledad") || tituloMinusculas.contains("soledad") -> {
                obtenerCapitulosCienAnosDeSoledad()
            }
            tituloMinusculas.contains("nombre del viento") -> {
                obtenerCapitulosNombreDelViento()
            }
            tituloMinusculas.contains("duna") || tituloMinusculas.contains("dune") -> {
                obtenerCapitulosDuna()
            }
            tituloMinusculas.contains("1984") -> {
                obtenerCapitulosMilNovecientosOchentaYCuatro()
            }
            tituloMinusculas.contains("hábitos atómicos") || tituloMinusculas.contains("habitos") -> {
                obtenerCapitulosHabitosAtomicos()
            }
            tituloMinusculas.contains("sombra del viento") -> {
                obtenerCapitulosSombraDelViento()
            }
            tituloMinusculas.contains("fahrenheit") -> {
                obtenerCapitulosFahrenheit()
            }
            tituloMinusculas.contains("señor de los anillos") || tituloMinusculas.contains("anillos") -> {
                obtenerCapitulosSenorDeLosAnillos()
            }
            else -> {
                obtenerCapitulosGenericos(libro)
            }
        }
    }

    private fun obtenerCapitulosCienAnosDeSoledad(): List<CapituloModelo> = listOf(
        CapituloModelo(
            numero = 1,
            titulo = "Capítulo I — La aldea de Macondo",
            parrafos = listOf(
                "Muchos años después, frente al pelotón de fusilamiento, el coronel Aureliano Buendía había de recordar aquella tarde remota en que su padre lo llevó a conocer el hielo.",
                "Macondo era entonces una aldea de veinte casas de barro y cañabrava construidas a la orilla de un río de aguas diáfanas que se precipitaban por un lecho de piedras pulidas, blancas y enormes como huevos prehistóricos.",
                "El mundo era tan reciente, que muchas cosas carecían de nombre, y para mencionarlas había que señalarlas con el dedo. Todos los años, por el mes de marzo, una familia de gitanos desarrapados plantaba su carpa cerca de la aldea, y con un grande alboroto de pitos y timbales daban a conocer los nuevos inventos.",
                "Primero llevaron el imán. Un gitano corpulento, de barba montaraz y manos de gorrión, que se presentó con el nombre de Melquíades, hizo una truculenta demostración pública de lo que él mismo llamaba la octava maravilla de los sabios alquimistas de Macedonia.",
                "José Arcadio Buendía, cuya desaforada imaginación iba siempre más lejos que el ingenio de la naturaleza, y aun más allá del milagro y la magia, pensó que era posible servirse de aquella invención inútil para desenterrar el oro de la tierra."
            )
        ),
        CapituloModelo(
            numero = 2,
            titulo = "Capítulo II — El galeón encallado",
            parrafos = listOf(
                "Durante varios meses, José Arcadio Buendía se dejó arrastrar por la fiebre del imán. Exploró palmo a palmo la región, incluso el fondo del río, arrastrando los dos lingotes de hierro y recitando en voz alta el conjuro de Melquíades.",
                "Lo único que logró desenterrar fue una armadura del siglo XV con todas sus partes soldadas por un cascote de óxido, cuyo interior producía una resonancia hueca de calabazo lleno de piedras.",
                "Cuando José Arcadio Buendía y los cuatro hombres de su expedición lograron desarticular la armadura, encontraron dentro un esqueleto calcificado que llevaba colgado en el cuello un relicario de cobre con un rizo de mujer.",
                "Úrsula Iguarán, su laboriosa esposa, solía lamentar que su marido hubiera invertido los ahorros de toda una vida en aquellos artilugios sin porvenir, mientras la casa requería reparaciones urgentes y el pan de cada día escaseaba."
            )
        ),
        CapituloModelo(
            numero = 3,
            titulo = "Capítulo III — El astrolabio y la peste del olvido",
            parrafos = listOf(
                "En marzo volvieron los gitanos. Esta vez llevaban un catalejo y una lupa del tamaño de un tambor, que exhibieron como el último descubrimiento de los judíos de Ámsterdam.",
                "Sentaron a una gitana en un extremo de la aldea e instalaron el catalejo a la entrada de la carpa. Mediante el pago de cinco reales, la gente se asomaba al catalejo y veía a la gitana al alcance de la mano.",
                "«La ciencia ha eliminado las distancias», pregonaba Melquíades. «Dentro de poco, el hombre podrá ver lo que ocurre en cualquier lugar de la tierra, sin moverse de su casa».",
                "José Arcadio Buendía no descansó hasta canjear los dos lingotes imantados y tres piezas de dinero colonial por la lupa gigante, seguro de que con ella crearía el arma solar definitiva."
            )
        )
    )

    private fun obtenerCapitulosNombreDelViento(): List<CapituloModelo> = listOf(
        CapituloModelo(
            numero = 1,
            titulo = "Prólogo — Un silencio de tres partes",
            parrafos = listOf(
                "Era de noche. La posada Roca de Guía estaba en silencio, y era un silencio de tres partes.",
                "La parte más obvia era un silencio hueco y resonante, fabricado a partir de las cosas que faltaban. Si hubiera soplado el viento, este habría suspirado entre las ramas, habría hecho chirriar el cartel de la posada en sus goznes y habría barrido las hojas por el camino como si fueran confeti.",
                "Si hubiera habido una multitud en la posada, o al menos un puñado de hombres, habrían llenado el silencio con conversaciones y risas, el tintineo de los vasos y el ruido metálico de las jarras contra las mesas de madera.",
                "La tercera parte no era fácil de percibir. Si escuchabas durante una hora entera, quizá empezaras a notarla en el suelo de madera y en las toscas tablas de la cama. Estaba en la pesada chimenea de piedra negra, que conservaba el calor de un fuego apagado hacía tiempo.",
                "El silencio era de Kvothe. El hombre que había esperado morir, y que ahora se disponía a contar la historia de su vida."
            )
        ),
        CapituloModelo(
            numero = 2,
            titulo = "Capítulo I — Un lugar para forasteros",
            parrafos = listOf(
                "El forastero entró en la posada sacudiéndose el polvo del camino de su abrigo de paño grueso. Traía consigo el cansancio de muchos kilómetros recorridos bajo el sol implacable de la primavera tardía.",
                "Detrás de la barra, Kote lustraba un vaso de cristal con un trapo inmaculadamente blanco. Sus movimientos eran pausados y precisos, como los de un hombre acostumbrado al ritmo constante y solitario del oficio.",
                "—Bienvenido a la Roca de Guía —dijo con voz suave y serena—. ¿Puedo ofrecerte algo de beber, viajero?",
                "—Cerveza negra si tienes —respondió el forastero, dejando caer una bolsa de cuero gastada sobre la madera desgastada del mostrador."
            )
        )
    )

    private fun obtenerCapitulosDuna(): List<CapituloModelo> = listOf(
        CapituloModelo(
            numero = 1,
            titulo = "Capítulo I — Arrakis, el planeta del desierto",
            parrafos = listOf(
                "Un comienzo es un momento muy delicado. Si quieres comprender la vida de Muad'Dib, debes situarlo primero en su tiempo: nacido en el año 57 del Emperador Padishah, Shaddam IV.",
                "Y debes situarlo sobre todo en su lugar: el planeta Arrakis. Y no te dejes engañar por el hecho de que fuera llamado también Dune, aunque ése fuera el nombre que el pueblo le daba.",
                "El planeta era el hogar de la especia melange, la sustancia más preciosa y codiciada de todo el universo conocido. Quien controlara la especia, controlaría el destino del imperio.",
                "En la penumbra previa al alba de Caladan, Paul Atreides esperaba despierto en sus aposentos. Sabía que aquel día marcaría la despedida definitiva del mundo de agua que lo vio nacer."
            )
        ),
        CapituloModelo(
            numero = 2,
            titulo = "Capítulo II — La prueba de la caja del dolor",
            parrafos = listOf(
                "—Pon tu mano derecha en la caja —ordenó la anciana Reverenda Madre Gaius Helen Mohiam.",
                "Paul sintió el impulso de negarse, pero la Voz contenida en las palabras de la mujer doblegaba su voluntad con la fuerza de un resorte de acero templado.",
                "—¿Qué hay dentro? —preguntó Paul.",
                "—Dolor —respondió ella—. Solo dolor. No apartes la mano, porque el gom jabbar te espera en el cuello. Quien retira la mano antes de tiempo es un animal; el humano soporta el fuego para preservar su ser."
            )
        )
    )

    private fun obtenerCapitulosMilNovecientosOchentaYCuatro(): List<CapituloModelo> = listOf(
        CapituloModelo(
            numero = 1,
            titulo = "Capítulo I — El Gran Hermano te vigila",
            parrafos = listOf(
                "Era un día luminoso y frío de abril y los relojes daban las trece. Winston Smith, con la barbilla clavada en el pecho en su empeño por burlar el viento repugnante, se deslizó rápidamente por las puertas de cristal de las Casas de la Victoria.",
                "En el rellano del piso, frente al hueco del ascensor, el cartelón con el enorme rostro miraba fijamente desde la pared. Era una de esas imágenes hechas de tal forma que los ojos le siguen a uno a dondequiera que vaya.",
                "«EL GRAN HERMANO TE VIGILA», decían las letras al pie de la imagen.",
                "Dentro del apartamento, una voz melosa leía una serie de cifras sobre la producción de arrabio. La voz salía de una placa oblonga de metal, una especie de espejo empañado que formaba parte de la pared de la derecha. Winston giró una clavija y la voz disminuyó de volumen, aunque las palabras aún se distinguían."
            )
        )
    )

    private fun obtenerCapitulosHabitosAtomicos(): List<CapituloModelo> = listOf(
        CapituloModelo(
            numero = 1,
            titulo = "Capítulo I — El sorprendente poder de los pequeños hábitos",
            parrafos = listOf(
                "El destino del ciclismo británico cambió un día del año 2003. La organización deportiva contrataba a Dave Brailsford como su nuevo director de desempeño.",
                "Hasta ese momento, los ciclistas profesionales de Gran Bretaña habían soportado casi cien años de mediocridad. Desde 1908, los ciclistas británicos solo habían ganado una solitaria medalla de oro en los Juegos Olímpicos.",
                "La estrategia de Brailsford era simple: la agregación de ganancias marginales. Esta filosofía se basaba en buscar un margen diminuto de mejora en todo lo que se hacía.",
                "Si mejoras un uno por ciento cada día durante un año, terminarás siendo treinta y siete veces mejor al finalizar el ciclo. Por el contrario, si empeoras un uno por ciento cada día, tu hábito decaerá hasta llegar casi a cero."
            )
        )
    )

    private fun obtenerCapitulosSombraDelViento(): List<CapituloModelo> = listOf(
        CapituloModelo(
            numero = 1,
            titulo = "Capítulo I — El Cementerio de los Libros Olvidados",
            parrafos = listOf(
                "Todavía recuerdo aquel amanecer en que mi padre me llevó por primera vez a visitar el Cementerio de los Libros Olvidados. Desgranaban los primeros días del verano de 1945 y caminábamos por las calles de una Barcelona atrapada bajo cielos de ceniza.",
                "—Daniel, lo que vas a ver hoy no se lo puedes contar a nadie —me advirtió mi padre—. Ni a tu amigo Tomás, ni a nadie.",
                "Nos detuvimos ante un portón de madera carcomida y ennegrecida por el tiempo y la humedad. Mi padre empujó la pesada cancela y penetramos en un inmenso laberinto de estanterías pobladas de volúmenes centenarios.",
                "—Según la costumbre, la primera vez que alguien visita este lugar debe escoger un libro, el que prefiera, y adoptarlo, asegurándose de que nunca desaparecerá, de que siempre permanecerá vivo."
            )
        )
    )

    private fun obtenerCapitulosFahrenheit(): List<CapituloModelo> = listOf(
        CapituloModelo(
            numero = 1,
            titulo = "Capítulo I — Era un placer quemar",
            parrafos = listOf(
                "Era un placer quemar. Era un placer especial ver las cosas devoradas, ver las cosas ennegrecidas y cambiadas.",
                "Con la punta de bronce en sus puños, con esa gran pitón escupiendo su veneno inflamable sobre el mundo, la sangre le latía en la cabeza y sus manos eran las de un prodigioso director de orquesta que ejecutaba todas las sinfonías de fuego y de rescoldos.",
                "Guy Montag sonrió con la sonrisa feroz de todos los hombres quemados por las llamas ardientes. Sabía que cuando volviera al cuartel de bomberos se miraría en el espejo y se guiñaría un ojo complacido."
            )
        )
    )

    private fun obtenerCapitulosSenorDeLosAnillos(): List<CapituloModelo> = listOf(
        CapituloModelo(
            numero = 1,
            titulo = "Capítulo I — Una fiesta muy esperada",
            parrafos = listOf(
                "Cuando el señor Bilbo Bolsón de Bolsón Cerrado anunció que pronto celebraría su cumpleaños centésimo decimoprimero con una fiesta de especial magnificencia, hubo muchos comentarios y gran expectación en Hobbiton.",
                "Bilbo era inmensamente rico y muy peculiar, y había sido la admiración de la Comarca durante sesenta años, desde su memorable desaparición y su inesperado regreso cargado de oro y plata.",
                "Y si esto no fuera suficiente para la fama de Bilbo, estaba además su prolongado vigor: el tiempo pasaba, pero parecía no tener efecto en el señor Bolsón. A los noventa años tenía el mismo aspecto que a los cincuenta."
            )
        )
    )

    private fun obtenerCapitulosGenericos(libro: LibroModelo): List<CapituloModelo> {
        val descripcion = if (libro.descripcion.isNotBlank()) libro.descripcion else "Una fascinante obra literaria que cautiva desde sus primeras líneas, explorando la profundidad humana y la imaginación a través de las palabras."
        val autor = libro.obtenerTextoAutores()
        return listOf(
            CapituloModelo(
                numero = 1,
                titulo = "Capítulo 1 — Introducción y Primeras Páginas",
                parrafos = listOf(
                    "«${libro.titulo}» se abre ante nosotros como un portal hacia una experiencia inolvidable, obra magistral de $autor.",
                    descripcion,
                    "Las palabras iniciales sientan las bases de una narrativa envolvente, donde cada detalle ha sido cuidadosamente forjado para despertar la curiosidad y la reflexión de quien se adentra en sus páginas.",
                    "A medida que avanzamos, los personajes cobran vida propia, guiándonos por senderos de conocimiento, emoción y descubrimiento que perduran en la memoria mucho después de cerrar el libro."
                )
            ),
            CapituloModelo(
                numero = 2,
                titulo = "Capítulo 2 — El Núcleo de la Trama",
                parrafos = listOf(
                    "El conflicto central comienza a tomar forma con una intensidad creciente, desafiando las expectativas construidas al inicio.",
                    "Los diálogos revelan matices sutiles y dilemas morales que conectan con los aspectos más universales de la condición humana.",
                    "En este punto, la obra demuestra su verdadero alcance, consolidando su lugar en la biblioteca personal de quienes aprecian la buena literatura."
                )
            ),
            CapituloModelo(
                numero = 3,
                titulo = "Capítulo 3 — Reflexión y Culminación",
                parrafos = listOf(
                    "La travesía literaria alcanza su punto culminante, entrelazando las lecciones del camino con un desenlace que invita a la contemplación.",
                    "«Leer es viajar sin moverse del sitio, es vivir mil vidas en una sola existencia.»",
                    "Has completado este recorrido de lectura en BooksApp. Te invitamos a marcarlo como leído y continuar explorando nuevos horizontes literarios."
                )
            )
        )
    }
}
