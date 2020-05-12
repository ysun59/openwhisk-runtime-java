set terminal pdfcairo dashed font "Gill Sans,10" linewidth 2 rounded fontscale 1.0

# Line style for axes
set style line 80 lt rgb "#808080"

# Line style for grid
set style line 81 lt 0  # dashed
set style line 81 lt rgb "#808080"  # grey
# set missing "?"

set grid back linestyle 81
set border 3 back linestyle 80
set xtics nomirror
set ytics nomirror

set logscale x

set output "fig_slowdown_filter.pdf"
set ylabel "CDF" font ",9" offset 2.5
set xlabel "Regular JCT / Photons JCT" font ",9" #offset 2

#unset key
#set key top left outside
#set key title "# direct links"
set key inside bottom right font ",8"
#set key above font ",7" horizontal
#set key spacing 1.5 samplen 0.5 height 0.7
#unset key

set xtics font ",9"
set format x "%Hx"

set ytics font ",9" 0,.2,1.0
#set style line 1 lt 1 lw 0.5
#set xrange[0:]
#set yrange[0:]
#set xrange[1:2.1]
#set xrange [0:1]
set yrange[0:1]


plot \
  "pd_slowdown_poisson.csv" using 1:2 title "Poisson" with lines lc rgb "blue" lw 2 lt 1, \
  "pd_slowdown_markov.csv" using 1:2 title "Markov" with lines dashtype '.____.' lc rgb "orange" lw 3 lt 2
