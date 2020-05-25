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

set output "output/app_memory_breakdown_line.pdf"
set ylabel "Norm. memory utilization" font ",9" offset 1.5
set xlabel "Concurrency factor" font ",9" #offset 2

#unset key
#set key top left outside
#set key title "# direct links"
set key at 45.5,99.9 font ",8"
#set key above font ",7" horizontal
#set key spacing 1.5 samplen 0.5 height 0.7
#unset key

#set xtics font ",9"
#set format x "%Hx"

set ytics font ",9" 0,20,100.0
#set style line 1 lt 1 lw 0.5
#set xrange[0:]
#set yrange[0:]
#set xrange[1:2.1]
#set xrange [0:1]
set yrange[0:100]


plot \
  "data/data.txt" using 1:1 title "Baseline" with lines lc rgb "blue" lw 2 lt 1, \
  "data/data.txt" using 1:6 title "Video" with lines dashtype '...' lc rgb "brown" lw 3 lt 2, \
  "data/data.txt" using 1:4 title "Image" with lines dashtype '._._.' lc rgb "purple" lw 3 lt 2, \
  "data/data.txt" using 1:5 title "File hashing" with lines dashtype '____' lc rgb "pink" lw 3 lt 2, \
  "data/data.txt" using 1:3 title "REST" with lines dashtype '__.__' lc rgb "red" lw 3 lt 2, \
  "data/data.txt" using 1:2 title "Sleep" with lines dashtype '.____.' lc rgb "orange" lw 3 lt 2

  # "data/data.txt" using 1:7 title "Thumbnail" with lines dashtype '._.' lc rgb "green" lw 3 lt 2, \

