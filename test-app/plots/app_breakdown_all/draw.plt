# Note you need gnuplot 4.4 for the pdfcairo terminal.
#set terminal pdfcairo font "Helvetica,8" linewidth 4 rounded
#set size ratio 0.6
#set terminal postscript monochrome font "Helvetica, 22" linewidth 4 rounded
set terminal pdfcairo dashed font "Gill Sans,10" linewidth 2 rounded fontscale 1.0
set parametric

# Line style for axes
set style line 80 lt rgb "#808080"

# Line style for grid
set style line 81 lt 0  # dashed
set style line 81 lt rgb "#808080"  # grey
# set missing "?"

set grid back linestyle 81
unset grid #no xtics
set grid ytics
set border 3 back linestyle 80 # Remove border on top and right.  These
             # borders are useless and make it harder
                          # to see plotted lines near the border.
                              # Also, put it in grey; no need for so much emphasis on a border.
                              set xtics nomirror
                              set ytics nomirror

set output "./output/app_memory_breakdown_all.pdf"
set ylabel "Norm. memory utilization" font ",9" offset 1.5
#set xlabel "Functions co-executed" font ",9" #offset 2

#set key top left inside font ",9"
set key horiz
set key top center outside font ",9"
#set key outside bottom right font ",9"
set key spacing 1.0 samplen 1.5 height 0.5
# unset key

#set xtics font ",9" center offset 0,-0.11 rotate by 45 right
set ytics font ",9" 0,0.2,1
#set style line 1 lt 1 lw 0.5
#set xrange [0:]
set yrange[0:1]
set xrange[-0.5:1.5]
#set logscale x
#set yrange[0:1.5]
set xtics("Sharable" 0, "Private" 1)

#set style data histogram #rowstacked
#set style histogram rowstacked
set style histogram cluster gap 1
set boxwidth 1 absolute

set style fill pattern border 0 #lt 1 lw 1 lc rgb "black"

set tic scale 0

plot \
  "./data/rest.txt" using (($2)) title "REST" with histogram lc rgb "#A52A2A" fs pattern 1, \
  "./data/sleep.txt" using (($2)) title "Sleep" with histogram lc rgb "red"  fs pattern 5, \
  "./data/file_hashing.txt" using (($2)) title "File hashing" with histogram lc rgb "#7FFFD4" fs pattern 9, \
  "./data/image_classification.txt" using (($2)) title "Image Class."  with histogram lc rgb "#D2691E" fs pattern 10, \
  "./data/video.txt" using (($2)) title "Video" with histogram lc rgb "#006400" fs pattern 4

#"./tmp/mean_fct_fifo.txt" using (($3/1000)) title "Oblivious" with histogram lc rgb "blue" fs pattern 2, \

# plot \
#   "./tmp/mean_fct_fifo.txt" using (($3/1000)) title "Oblivious" lc rgb "blue" fs pattern 2, \
#   "./tmp/mean_fct_perfect.txt" using (($3/1000)) title "Perfect" lc rgb "#A52A2A" fs pattern 1, \
#   "./tmp/mean_fct_50_00.txt" using (($3/1000)) title "GBDT" lc rgb "red"  fs pattern 5 , \
#   "./tmp/mean_fct_buffer.txt" using (($3/1000)) title "Buffer" lc rgb "#006400" fs pattern 4, \
#   "./tmp/mean_fct_mean.txt" using (($3/1000)) title "Mean" lc rgb "#7FFFD4" fs pattern 9, \
#   "./tmp/mean_fct_seq.txt" using (($3/1000)) title "Aging" lc rgb "#D2691E" fs pattern 10
