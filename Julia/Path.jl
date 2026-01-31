using Random



mutable struct Path
    cities :: Vector{Int}
    fitness ::Float64

   
    function Path(weights ::  Vector{Vector{Int}},rng)
       
        n = length(weights)
        cities  = collect(1:n)
        shuffle(cities,rng);
        obj =new(cities,0)
        calcFitness(obj, weights)
        return obj

    end
    function Path(size :: Int)
        cities = fill(-1, size)
        obj = new(cities, 0.0)
        return obj
    end
    function Path(weights ::  Vector{Vector{Int}}, index :: Int)
        cities = Vector{Int}()
        push!(cities,index)
        bestConnection = typemax(Int)
        bestVertex  = -1
        size = length(weights)

        for i in 1:(size-1)
            bestConnection = typemax(Int)
            bestVertex = -1
            for k in 1:size
            if !(k in cities) && bestConnection > weights[cities[i]][k]  
                bestConnection = weights[last(cities)][k]
                bestVertex = k
                end
            end
            push!(cities,bestVertex)
        end

        obj =new(cities,0)
        calcFitness(obj, weights)
        return obj
    end
end


function shuffle(list::Vector{Int}, rng)
    n = length(list)
    for i in n-1:-1:1
        j = Int(mod(nextLong(rng), i + 1)) + 1  
        list[i+1], list[j] = list[j], list[i+1]  
    end
    return list
end

function calcFitness(path::Path, weights::Vector{Vector{Int}})
    result :: Int= 0
    @inbounds @simd for i in 1:length(path.cities)-1
        result += weights[path.cities[i]][path.cities[i+1]]
    end
    result += weights[last(path.cities)][first(path.cities)]
    path.fitness = 1.0 / result
end



function calcCost(path :: Path,weights ::  Vector{Vector{Int}})
    result :: Int =0 
    for i in 1:(length(path.cities)-1)
        result += weights[path.cities[i]][path.cities[i+1]]
    end

    result += weights[last(path.cities)][first(path.cities)]
    return result
    
end