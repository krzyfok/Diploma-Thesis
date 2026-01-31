mutable struct SplitMix64
    x::Int64
end

const C1 = reinterpret(Int64, UInt64(0x9E3779B97F4A7C15))
const C2 = reinterpret(Int64, UInt64(0xBF58476D1CE4E5B9))
const C3 = reinterpret(Int64, UInt64(0x94D049BB133111EB))

function next(sm::SplitMix64)
    sm.x += C1
    z = sm.x
    z = (z ⊻ (z >>> 30)) * C2
    z = (z ⊻ (z >>> 27)) * C3
    return z ⊻ (z >>> 31)
end
