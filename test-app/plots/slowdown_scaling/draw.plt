set terminal pdfcairo dashed font "Gill Sans,10" linewidth 2 rounded fontscale 1.0

# Line style for axes
set style line 80 lt rgb "#808080"

# Line style for grid
set style line 81 lt 0  # dashed
set style line 81 lt rgb "#cccccc"  # grey
# set missing "?"

set grid back linestyle 81
set border 3 back linestyle 80
set xtics nomirror
set ytics nomirror

#set logscale x

set output "output/slowdown_scaling.pdf"
set ylabel "Ratio" font ",9" offset 0.5
set xlabel "Concurrent invocations" font ",9" #offset 2

unset key
#set key bottom right inside font ",8"
#set key title "# direct links"
#set key at 275.,1.49 font ",9"
#set key above font ",7" horizontal
#set key spacing 1.5 samplen 0.5 height 0.7
#unset key

#set xtics font ",9"
#set format x "%Hx"

set ytics font ",9"
set ytics (1,5,10,15,20,25,30,35)
#set style line 1 lt 1 lw 0.5
#set xrange[0:]
#set yrange[0:]
#set xrange[1:2.1]
set xrange [0:160]
set yrange[0.0:35]

set format y "%Hx"

set label 1 "Execution time (regular/photons)" at 50,5 font ",9"
set label 2 "Memory(regular/photons)" at 50,20 font ",9"


plot \
  "data/data.txt" using 1:3 title "JCT(regular/photons)" with lines lc rgb "blue" lw 2 lt 1, \
  "data/data.txt" using 1:4 title "Memory(regular/photons)" with lines dashtype '____' lc rgb "orange" lw 3 lt 2

