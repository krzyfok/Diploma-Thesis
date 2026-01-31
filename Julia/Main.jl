include("DataLoader.jl")
include("Alg.jl")
# Etap 2
generationLimit = 20_000
eliteSize = 5
mR = 0.01
cR = 0.95
tournamentSize = 2

files = ["ftv47"]
seeds = [(i+1)^2 for i in 0:49]

# Ftv170
# combinations = [
#     Dict("initialization"=>"hybrid", "selection"=>"roulette", "crossing"=>"OX", "mutation"=>"swap", "populationSize"=>1000),
#     Dict("initialization"=>"hybrid", "selection"=>"roulette", "crossing"=>"OX", "mutation"=>"inversion", "populationSize"=>1000),
#     Dict("initialization"=>"hybrid", "selection"=>"roulette", "crossing"=>"OX", "mutation"=>"swap", "populationSize"=>2000),
#     Dict("initialization"=>"hybrid", "selection"=>"roulette", "crossing"=>"OX", "mutation"=>"inversion", "populationSize"=>2000)
# ]
#Ftv 47
combinations = [
    Dict("initialization"=>"random", "selection"=>"roulette", "crossing"=>"OX", "mutation"=>"swap", "populationSize"=>1000),
    Dict("initialization"=>"random", "selection"=>"roulette", "crossing"=>"OX", "mutation"=>"swap", "populationSize"=>2000),
    Dict("initialization"=>"random", "selection"=>"roulette", "crossing"=>"OX", "mutation"=>"inversion", "populationSize"=>2000)
]


for name in files
    fileName = name * ".atsp"
    graph = loadData(fileName)

    runCounter = 1

    for combo in combinations
       
        resFileName = "$(name)_init=$(combo["initialization"])_sel=$(combo["selection"])_cross=$(combo["crossing"])_mut=$(combo["mutation"])_pop=$(combo["populationSize"]).csv"

        println("Uruchamianie kombinacji parametrów do pliku: $resFileName")

        for seed in seeds
            println("  Run $(runCounter) | seed=$(seed)")
            startAlg(
                resFileName,
                runCounter,
                graph,
                combo["populationSize"],
                generationLimit,
                mR,
                cR,
                eliteSize,
                combo["selection"],
                combo["mutation"],
                combo["crossing"],
                seed,
                combo["initialization"],
                tournamentSize
            )
            runCounter += 1
        end
    end
end


# include("DataLoader.jl")
# include("Alg.jl")


# #Etap 1
# generationLimit = 20_000
# eliteSize = 5
# mR = 0.01
# cR = 0.95
# tournamentSize = 2

# files = [ "ftv47"]

# for name in files
# fileName = name *".atsp"
# graph = loadData(fileName)


# if occursin("47", fileName)
#     populationSizes = [1000, 2000]
# elseif occursin("170", fileName)
#     populationSizes = [1000, 2000]
# elseif occursin("443", fileName)
#     populationSizes = [1000, 2000]
# else
#     populationSizes = [1000]
# end


# initializations = ["random", "hybrid"]
# selections = ["tournament", "roulette"]
# crossovers = ["OX", "TPX"]
# mutations = ["swap", "inversion"]
# seeds = [2, 3, 5, 8, 13, 21, 34, 55, 89, 144]


# let 
#  runCounter = 1


# for popSize in populationSizes
#     for init in initializations
#         for select in selections
#             for cross in crossovers
#                 for mut in mutations
#                     for seed in seeds
#                         println("Run $(runCounter) | init=$(init) | sel=$(select) | cross=$(cross) | mut=$(mut) | seed=$(seed)")

#                         startAlg(
#                             name *"res.csv",
#                             runCounter,
#                             graph,
#                             popSize,
#                             generationLimit,
#                             mR,
#                             cR,
#                             eliteSize,
#                             select,
#                             mut,
#                             cross,
#                             seed,
#                             init,
#                             tournamentSize
#                         )

#                         runCounter += 1
#                     end
#                 end
#             end
#         end
#     end
# end
# end
# end
