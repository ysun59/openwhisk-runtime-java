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

set output "fig_utilization.pdf"
set ylabel "Cluster utilization" font ",9" #offset 2.5
set xlabel "Time (s)" font ",9" #offset 2

unset key
#set key inside top left font ",8"

set xtics font ",9"
#set format x "%H"
set format y "%.0f%%"

set ytics font ",9" 0,20,100
#set xrange [0:1]
set yrange[0:100]

set label 1 "regular" at 100,78 font ",9"
set label 2 "photons" at 100,36 font ",9"


plot \
  "data/pd_utilization.csv" using 1:(100*$3) title "regular" with lines lc rgb "blue" lw 2 lt 1, \
  "data/pd_utilization.csv" using 1:(100*$2) title "photons" with lines dashtype '.____.' lc rgb "orange" lw 3 lt 2
