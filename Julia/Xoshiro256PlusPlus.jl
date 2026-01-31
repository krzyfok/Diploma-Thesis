include("SplitMix64.jl")
mutable struct Xoshiro256PlusPlus
    s::NTuple{4, Int64}
end
function Xoshiro256PlusPlus(seed::Int64)
    sm = SplitMix64(seed)
    x0 = next(sm)
    x1 = next(sm)
    x2 = next(sm)
    x3 = next(sm)
   
    if x0 == 0 && x1 == 0 && x2 == 0 && x3 == 0
        error("Error")
    end

    return Xoshiro256PlusPlus((x0, x1, x2, x3))
end
function rotl(x::Int64, k::Int)
    return (x << k) | (x >>> (64 - k))  
end

function nextLong(rng::Xoshiro256PlusPlus)
    s0, s1, s2, s3 = rng.s
    result = rotl(s0 + s3, 23) + s0

    t = s1 << 17

    s2 = s2 ⊻ s0
    s3 = s3 ⊻ s1
    s1 = s1 ⊻ s2
    s0 = s0 ⊻ s3
    s2 = s2 ⊻ t
    s3 = rotl(s3, 45)

    rng.s = (s0, s1, s2, s3)
    return result
end

function nextInt(rng::Xoshiro256PlusPlus)
    r = nextLong(rng)
    return Int32((r >> 32) & 0x7FFFFFFF) 
end

function nextDouble(rng::Xoshiro256PlusPlus)
    
    raw = nextLong(rng)
    shifted = raw >>> 11
    return Float64(shifted) / (1 << 53)
end


