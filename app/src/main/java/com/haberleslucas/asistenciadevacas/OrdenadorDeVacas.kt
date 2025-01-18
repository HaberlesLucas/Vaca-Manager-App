package com.haberleslucas.asistenciadevacas

// Usando generics para poder manejar ambos modelos
class OrdenadorDeVacas {
    companion object {
        // Ordenación para cualquier tipo de modelo
        fun <T> ordenarPorId(vacas: MutableList<T>, idExtractor: (T) -> Int) {
            vacas.sortBy { idExtractor(it) }
        }

        fun <T> ordenarPorNombreAZ(vacas: MutableList<T>, nombreExtractor: (T) -> String?) {
            vacas.sortBy { nombreExtractor(it)?.lowercase() }
        }

        fun <T> ordenarPorNombreZA(vacas: MutableList<T>, nombreExtractor: (T) -> String?) {
            vacas.sortByDescending { nombreExtractor(it)?.lowercase() }
        }
    }
}
