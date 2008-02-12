#/opt/local/bin/ruby

#
# lzx class name converter
#
# togawamanabu <togawamanabu@gmail.com>
#

require "rexml/document"
require "rexml/parsers/pullparser"
require "fileutils"
require 'csv'

require 'pp'

def convert_class_from_lzx(lzxfile_path)
  original_file = File.open(lzxfile_path)

  p lzxfile_path

  #filenamechange
  after_file_path = lzxfile_path
  @convert_classes.each_pair do |before, after|
    if lzxfile_path.include?(before)
      after_file_path.gsub!(Regexp.new(before), after) if lzxfile_path.include?(before)
    end
  end

  converted_file_dir = @expand_outputdir + "/" + File.dirname(after_file_path)
  FileUtils.mkdir_p(converted_file_dir) unless File.exist?(converted_file_dir)
  converted_file = File.open(@expand_outputdir + "/" + after_file_path, "w")

  original_file.each do |line|
    @convert_classes.each_pair do |before, after|
      if line.include?(before)
        line.gsub!(Regexp.new(before), after) if line.include?(before)
      end
    end
    converted_file.puts(line)
  end
  original_file.close
end

# main
@@private_keyword = "@keywords private"

#basedir to convert classes
@basedir = ARGV[0] || "."

#csv file
@csvfile = ARGV[1] || "class_names/class_name.csv"

#outputdir for lzxdoc
@outputdir = ARGV[2] || "converted_lzx"

@rlzxdoc_root = File.dirname(File.expand_path(__FILE__))

@lzx_classes = Array.new

@expand_outputdir = File.expand_path(@outputdir)

#cleanup
FileUtils.rm_r(@expand_outputdir) if File.exist?(@expand_outputdir)
Dir.mkdir(@expand_outputdir)

Dir::chdir(@basedir)

#get list from csv
@convert_classes = Hash.new
CSV.open(@csvfile, 'r') do |row|
  @convert_classes[row[0]] = row[1]
end

Dir::glob("*.lzx").each{ |lzxfile| convert_class_from_lzx(lzxfile) }

Dir::glob("**/").each { |dir|
  Dir::glob("#{dir}*.lzx").each {|lzxfile|
    convert_class_from_lzx(lzxfile)
  }
}
