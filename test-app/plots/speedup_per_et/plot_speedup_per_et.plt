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

set logscale x
set logscale y

set output "fig_speedup_per_et.pdf"
set xlabel "Execution time(ms)" font ",9" offset 2.5
set ylabel "Avg. speedup" font ",9" #offset 2

#unset key
#set key top left outside
#set key title "# direct links"
#set key inside top right font ",8"
#set key above font ",7" horizontal
#set key spacing 1.5 samplen 0.5 height 0.7
#unset key

set xtics font ",9"
set format y "%Hx"
set xtics ("1" 1, "10" 10, "100" 100, "10^3" 1000, "10^4" 10000, "10^5" 100000, "10^6" 1000000)

set ytics font ",9"
#set ytics (1,4,8,12,16)
#set style line 1 lt 1 lw 0.5
#set xrange[0:]
#set yrange[0:]
#set xrange[1:2.1]
#set xrange [0:1]
set yrange[1:]


plot \
  "data.txt" using (($1)*1000):3 notitle with lines lc rgb "blue" lw 2 lt 1
  #"data.txt" using (($1)*1000):2 title "Gaussian" with lines lc rgb "orange" lw 3 lt 2
