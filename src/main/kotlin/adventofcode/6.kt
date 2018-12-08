package adventofcode

import java.io.File

fun main(args: Array<String>) {

    val locationsRaw = File("src/main/resources/6.txt").readLines()

    val symbols = "abcdefghijklmnopqrstuvwxyzåäö_abcdefghijklmnopqrstuvwxyzåäö"
    var c = 0
    val locations = locationsRaw.map { row ->
        val coords = row.trim().split(", ")
        Location(Coord(Integer.valueOf(coords[0]), Integer.valueOf(coords[1])), symbols[c++])
    }

    val boundaries = getBoundaries(locations.map { it.coord })
    println("Boundaries: " + boundaries.xmin + ", " + boundaries.ymin + ", " + boundaries.xmax + ", " + boundaries.ymax)

    val dotLocation = Location(Coord(-1,-1), '.')
    val matrix = mutableListOf<List<Location>>()
    val notFinite = mutableSetOf<Location>()

    for (y in boundaries.xmin..boundaries.xmax) {
        val row = mutableListOf<Location>()
        for (x in boundaries.xmin..boundaries.xmax) {
            val locationDistances = locations.map { loc -> Pair(loc, getDistance(loc.coord, Coord(x,y))) }.sortedBy { it.second }
            if(locationDistances[0].second == locationDistances[1].second) {
                row.add(dotLocation)
            } else {
                row.add(locationDistances[0].first)
                if(x == boundaries.xmin || x == boundaries.xmax || y == boundaries.ymin || y == boundaries.ymax) {
                    notFinite.add(locationDistances[0].first)
                }
            }
        }
        matrix.add(row);
        //println(row.map { it.number })
    }

    val result = matrix.flatten()
        .filter { !notFinite.contains(it) && !it.equals(dotLocation) }
        .groupBy { it.coord }
        .toList()
        .sortedByDescending { it.second.size }
        .map { Pair(it.first, it.second.size) }[0]

    println("Result part 1: " + result.first.x + ", " + result.first.y + ": " + result.second)


    val closeLocations = mutableListOf<Pair<Coord,Int>>()
    for (y in boundaries.xmin..boundaries.xmax) {
        for (x in boundaries.xmin..boundaries.xmax) {
            val distanceSum = locations.map { location -> getDistance(location.coord, Coord(x,y)) }.sum()
            if( distanceSum < 10000) {
                closeLocations.add(Pair(Coord(x,y),distanceSum))
            }
        }
    }

    println("# close coords:" + closeLocations.size)
}

class Boundaries(var xmin: Int, var ymin: Int, var xmax: Int, var ymax: Int)
class Coord(val x: Int, val y: Int)
class Location(val coord: Coord, val number: Char)

fun getBoundaries(locations: List<Coord>): Boundaries {
    return locations.fold(Boundaries(10000, 100000, -1, -1)) { bounds, coord ->
        if (coord.x < bounds.xmin)
            bounds.xmin = coord.x
        if (coord.y < bounds.ymin)
            bounds.ymin = coord.y
        if (coord.x > bounds.xmax)
            bounds.xmax = coord.x
        if (coord.y > bounds.ymax)
            bounds.ymax = coord.y

        bounds
    }
}

fun getDistance(loc: Coord, coord: Coord): Int {
    return Math.abs(loc.x - coord.x) + Math.abs(loc.y - coord.y);
}





