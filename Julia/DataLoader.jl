function loadData(fileName :: String)
        data = join(readlines(fileName), " ")
        words = split(data)
        size = 0
        weights = Vector{Vector{Int}}()
        index = 0
       for i in 1:length(words)-1
            if words[i] == "DIMENSION:"
                size = parse(Int, words[i+1])
                break
            end
        end
        for i in 1:length(words)
            if words[i] =="EDGE_WEIGHT_SECTION"
                index = i +1;
                break
            end
        end

        for i in 1:size
            singleRow = Vector{Int}()
            for j in 1:size
                push!(singleRow, parse(Int, words[index]))
                index+=1;
            end
            push!(weights, singleRow)
        end

        return weights
end

