include("Path.jl")
include("Xoshiro256PlusPlus.jl")



using Printf
using Random
function startAlg(fileName:: String,iter :: Int, weights ::  Vector{Vector{Int}},  populationSize :: Int,  generationLimit :: Int64,  mR :: Float64, cR :: Float64 ,  elite::Int,selection :: String,mutation ::String,crossover ::String, seed :: Int, initial:: String,tSize::Int )
    GC.gc()

    
    
    size :: Int = length(weights)
    bestPathValue :: Float64 = 0.0   
    bestCost :: Int = 0
    eliteSize :: Int = Int(elite * populationSize / 100)
    if eliteSize % 2 != 0
        eliteSize += 1
    end
   
    newPopSize::Int = populationSize - eliteSize
    generationNum::Int = 0

    if seed==0
        
        seed_rng = MersenneTwister(rand(Int64))
        state = ntuple(_ -> rand(seed_rng, Int64), 4)
        rng = Xoshiro256PlusPlus(state)
    else
        
        rng = Xoshiro256PlusPlus(Int64(seed))
    end
    population::Vector{Path} = Vector{Path}(undef, populationSize)
    newPopulation::Vector{Path} = Vector{Path}(undef, newPopSize)
    if initial == "hybrid"
        for i in 1:size
            population[i] = Path(weights,i)
        end
        for i in size+1:(populationSize)
            
            population[i] = Path(weights,rng)
        end
    else
        for i in 1:(populationSize)
            population[i] = Path(weights,rng)
        end
    end    
    
    max_mem = Ref(0)
    running = Ref(true)
    
    task =@async begin
        while running[]
            mem = Base.gc_live_bytes()
            if mem > max_mem[]
                max_mem[] = mem
            end
            sleep(0.1)
        end
    end
    startTime = time_ns()
    
    while generationNum<generationLimit
        
       generationNum+=1  
        @inbounds for i in 1:populationSize
            calcFitness(population[i],weights)
        end

        sort!(population, by = x -> x.fitness, rev = true)

        if bestPathValue<population[1].fitness
            bestPathValue =population[1].fitness
            bestCost = calcCost(population[1],weights)
         
        end

       
        if selection == "tournament"
            newPopulation = tournamentSelection(population, newPopSize,tSize,rng)
        else
            newPopulation = rouletteSelection(population, newPopSize,rng)
        end
        @inbounds for i in 1:2:newPopSize
            
            if nextDouble(rng)<cR
                if crossover == "OX"
                     children = crossingOX(newPopulation[i],newPopulation[i+1],rng)
                else
                     children = twoPointCrossover(newPopulation[i],newPopulation[i+1],rng)
                end
               
                newPopulation[i], newPopulation[i + 1] = children[1], children[2]
            end

            if mutation =="swap"

                newPopulation[i]=swapMutation(mR, newPopulation[i],rng)
                newPopulation[i+1]=swapMutation(mR, newPopulation[i+1],rng)

            else
                newPopulation[i]=inversionMutation(mR, newPopulation[i],rng)
                newPopulation[i+1]=inversionMutation(mR, newPopulation[i+1],rng)
            end
           
        end

        difference = populationSize - newPopSize

        @inbounds for i in 1:difference
            push!(newPopulation,population[i])
        end

        population= newPopulation


    end
    
    timeFinal = time_ns()-startTime
    sleep(0.2)
    running[] = false
    wait(task)
    println("Max memory: ",max_mem[] / (1024*1024))
    println("Best cost: ", bestCost)
    time_ms = timeFinal / 1_000_000.0
    println("Time:",(time_ms))
    

    
    file_exists = isfile(fileName)
    
    open(fileName, "a") do io
       
        if !file_exists || filesize(fileName) == 0
            println(io, "lp.;Time(ms);Cost;seed;populationSize;initialization;crossing;mutation;selection;cR;mR;eliteSize;generationLimit;tSize;Mem(Mb)")
        end
        
        
        @printf(io,
            "%d;%.6f;%d;%d;%d;%s;%s;%s;%s;%.2f;%.2f;%d;%d;%d;%f\n",
            iter, time_ms, bestCost, seed, populationSize,
            initial, crossover, mutation, selection,
            cR, mR, eliteSize, generationLimit, tSize, max_mem[] / (1024*1024)
        )
    end

    

    return Nothing
end


function crossingOX(parent1::Path, parent2::Path,rng) ::Vector{Path}
    size::Int = length(parent1.cities)
    child1::Path = Path(size)
    child2::Path = Path(size)
    result::Vector{Path} = Vector{Path}(undef, 2)

    crossPoint1::Int = mod(Int(nextInt(rng)), size) + 1
    crossPoint2::Int = mod(Int(nextInt(rng)), size) + 1

    while crossPoint1 == crossPoint2
        crossPoint2 = mod(Int(nextInt(rng)), size) + 1
    end
    if crossPoint1 > crossPoint2
        crossPoint1, crossPoint2 = crossPoint2, crossPoint1
    end
   
    set1 :: BitSet = BitSet()
    set2 :: BitSet = BitSet()
    @inbounds @simd for i in crossPoint1:crossPoint2
        val1 :: Int = parent1.cities[i]
        val2 :: Int = parent2.cities[i]
        child1.cities[i] = val1
        child2.cities[i] = val2
        push!(set1, val1)
        push!(set2, val2)
    end

    indexP = mod1(crossPoint2 + 1, size)
    indexC = indexP
    while indexC != crossPoint1
        val = parent2.cities[indexP]
        if !in(val, set1)
            child1.cities[indexC] = val
            indexC = mod1(indexC + 1, size)
            push!(set1, val)
        end
        indexP = mod1(indexP + 1, size)
    end

    indexP = mod1(crossPoint2 + 1, size)
    indexC  = indexP
    while indexC != crossPoint1
        val = parent1.cities[indexP]
        if !in(val, set2)
            child2.cities[indexC] = val
            indexC = mod1(indexC + 1, size)
            push!(set2, val)
        end
        indexP = mod1(indexP + 1, size)
    end

    result[1] = child1
    result[2] = child2
    return result
end
function twoPointCrossover(parent1::Path, parent2::Path,rng) ::Vector{Path}

    size::Int = length(parent1.cities)
    child1::Path = Path(size)
    child2::Path = Path(size)
    result::Vector{Path} = Vector{Path}(undef, 2)
    crossPoint1::Int =mod(Int(nextInt(rng)), size) + 1
    crossPoint2::Int = mod(Int(nextInt(rng)), size) + 1
    set1 :: BitSet = BitSet()
    set2 :: BitSet = BitSet()
    while crossPoint1 == crossPoint2
        crossPoint2 = mod(Int(nextInt(rng)), size) + 1
    end
    if crossPoint1 > crossPoint2
        crossPoint1, crossPoint2 = crossPoint2, crossPoint1
    end

    @inbounds @simd for i in 1:size
        if i<=crossPoint1 || i>=crossPoint2
            @inbounds child1.cities[i] = parent1.cities[i]
            @inbounds child2.cities[i] = parent2.cities[i]
            push!(set1, parent1.cities[i])
            push!(set2, parent2.cities[i])
        end
    end
    

    positionC1 :: Int = crossPoint1+1
    positionC2 :: Int = crossPoint1+1
    
    for i in 1:size

        value :: Int = parent2.cities[i]
        if !in(value, set1)
            child1.cities[positionC1]=value
            push!(set1, value)
            positionC1+=1

        end

        value  = parent1.cities[i]
        if !in(value, set2)
            child2.cities[positionC2]=value
            push!(set2, value)
            positionC2+=1

        end

    end

    result[1] = child1
    result[2] = child2
    return result
end

function swapMutation(mR :: Float64, individual :: Path,rng) :: Path
   
    size :: Int = length(individual.cities)
    if nextDouble(rng) <mR
        p1 :: Int = mod(Int(nextInt(rng)), size) + 1
        p2 :: Int = mod(Int(nextInt(rng)), size) + 1
        @inbounds individual.cities[p1], individual.cities[p2] = individual.cities[p2], individual.cities[p1]

    end

    return individual
end


function inversionMutation(mR :: Float64, individual :: Path,rng) :: Path
    
    size :: Int = length(individual.cities)
    if nextDouble(rng)  <mR
        p1 :: Int = mod(Int(nextInt(rng)), size) + 1
        p2 :: Int = mod(Int(nextInt(rng)), size) + 1
        
        if p1 > p2
           p1, p2 = p2, p1
        end

        @inbounds while p1 < p2
            individual.cities[p1], individual.cities[p2] = individual.cities[p2], individual.cities[p1]
            p1 += 1
            p2 -= 1
        end
    
    end

    return individual
    
end
function tournamentSelection(population ::  Vector{Path},newPopSize :: Int, tournamentSize :: Int,rng)::Vector{Path}
    selected ::Vector{Path}= Vector{Path}(undef, newPopSize)
    popSize ::Int = length(population)
    

     @inbounds for i in 1:newPopSize
        bestValue =0.0
        bestPath = nothing
         for k in 1:tournamentSize
            index = mod(Int(nextInt(rng)), popSize) + 1
            drawn = population[index]
            if drawn.fitness>bestValue
                bestValue = drawn.fitness
                bestPath = drawn
            end
        end
        
        selected[i]=bestPath
    end
    return selected
end

function rouletteSelection(population ::  Vector{Path},newPopSize :: Int,rng)::Vector{Path}
    selected ::Vector{Path}= Vector{Path}(undef, newPopSize)
    fitnessSum :: Float64 = 0.0
    
    @inbounds @simd for i in 1:length(population)
        fitnessSum += population[i].fitness
    end

     @inbounds for i in 1:newPopSize
        partialSum =0.0
        r :: Float64 = nextDouble(rng) * fitnessSum
        for path in population
        partialSum+=path.fitness
        if partialSum>=r
            selected[i]=path;
            break;

        end
    end
    end
    return selected
end
